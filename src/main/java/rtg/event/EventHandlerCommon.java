package rtg.event;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.IChunkGenerator;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.terraingen.WorldTypeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import rtg.RTGConfig;
import rtg.api.RTGAPI;
import rtg.api.util.BlockUtil;
import rtg.api.util.ChunkOreGenTracker;
import rtg.api.util.Logger;
import rtg.api.util.RandomUtil;
import rtg.api.util.UtilityClass;
import rtg.api.util.WorldUtil;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.world.biome.BiomeProviderRTG;
import rtg.world.gen.ChunkGeneratorRTG;


@UtilityClass
public final class EventHandlerCommon
{
    private EventHandlerCommon() {}

    public static void init() {
        MinecraftForge.TERRAIN_GEN_BUS.register(EventHandlerCommon.class);
        MinecraftForge.ORE_GEN_BUS.register(EventHandlerCommon.class); // to be removed with the ore gen tracker
    }


// TERRAIN_GEN_BUS

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInitBiomeGens(WorldTypeEvent.InitBiomeGens event) {
        // Remove the river GenLayer if we are in an RTG world.
        if (RTGAPI.checkWorldType(event.getWorldType())) {
            event.setNewBiomeGens(WorldUtil.Biomes.removeRivers(event.getNewBiomeGens()));
        }
    }

    @SubscribeEvent
    public static void onDecorateBiome(DecorateBiomeEvent.Decorate event) {

        RTGWorld rtgWorld = RTGWorld.getInstance(event.getWorld());
        if (rtgWorld.getGeneratorSettings().waterSpoutChance > 0 &&
            event.getWorld().getBiomeProvider() instanceof BiomeProviderRTG) {

            DecorateBiomeEvent.Decorate.EventType eventType = event.getType();
            if (eventType == Decorate.EventType.LAKE_WATER || eventType == Decorate.EventType.LAKE_LAVA) { // water & lava spouts
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void saplingGrowTreeRTG(SaplingGrowTreeEvent event) {

        final World world = event.getWorld();

        // skip if RTG saplings are disabled or this world does not use BiomeProviderRTG
        if (!RTGConfig.rtgTreesFromSaplings() || !(world.getBiomeProvider() instanceof BiomeProviderRTG)) {
            Logger.debug("[SaplingGrowTreeEvent] Aborting: RTG trees are disabled, or not an RTG dimension");
            return;
        }

        final BlockPos pos = event.getPos();
        final IBlockState saplingBlock = world.getBlockState(pos);
        Logger.trace("Handling SaplingGrowTreeEvent in dim: {}, at: {}, for: {}", world.provider.getDimension(), pos, saplingBlock);

        // Are we dealing with a sapling? Sounds like a silly question, but apparently it's one that needs to be asked.
        if (!(saplingBlock.getBlock() instanceof BlockSapling)) {
            Logger.debug("[SaplingGrowTreeEvent] Aborting: Sapling is not a sapling block ({})", saplingBlock.getBlock().getClass().getName());
            return;
        }

        final Random rand = event.getRand();

        // Should we generate a vanilla tree instead?
        int chance = RTGConfig.rtgTreeChance();
        if (rand.nextInt(chance < 1 ? 1 : chance) != 0) {
            Logger.debug("[SaplingGrowTreeEvent] Aborting RTG tree generation: random chance");
            return;
        }

        final IRealisticBiome rtgBiome = RTGAPI.getRTGBiome(world.getBiome(pos));
        Collection<TreeRTG> biomeTrees = rtgBiome.getTrees();

        if (biomeTrees.isEmpty()) {
            Logger.debug("[SaplingGrowTreeEvent] Aborting RTG tree generation: No RTG trees to generate in Biome: {}", rtgBiome.baseBiomeResLoc());
            return;
        }

        // First, let's get all of the trees in this biome that match the sapling on the ground.
        List<TreeRTG> validTrees = biomeTrees.stream()
            .filter(tree-> saplingBlock.getBlock() == tree.getSaplingBlock().getBlock() &&
                BlockUtil.getTypeFromSapling(saplingBlock) == BlockUtil.getTypeFromSapling(tree.getSaplingBlock()))
            .collect(Collectors.toList());

        // Abort if there are no valid trees.
        if (validTrees.isEmpty()) {
            Logger.debug("[SaplingGrowTreeEvent] No RTG trees found for sapling, so generating original tree instead");
            return;
        }

        // Get a random tree from the list of valid trees.
        TreeRTG tree = validTrees.get(rand.nextInt(validTrees.size()));

        // Set the trunk size if min/max values have been set.
        if (tree.getMinTrunkSize() > 0 && tree.getMaxTrunkSize() > tree.getMinTrunkSize()) {
            tree.setTrunkSize(RandomUtil.getRandomInt(rand, tree.getMinTrunkSize(), tree.getMaxTrunkSize()));
        }

        // Set the crown size if min/max values have been set.
        if (tree.getMinCrownSize() > 0 && tree.getMaxCrownSize() > tree.getMinCrownSize()) {
            tree.setCrownSize(RandomUtil.getRandomInt(rand, tree.getMinCrownSize(), tree.getMaxCrownSize()));
        }

        int treeHeight = tree.getTrunkSize() + tree.getCrownSize();
        if (treeHeight < 1) {
            Logger.debug("[SaplingGrowTreeEvent] Unable to grow RTG tree with no height: {}[logblock={}, leafblock={}, saplingblock={}]",
                tree.getClass().getSimpleName(), tree.getLogBlock(), tree.getLeavesBlock(), tree.getSaplingBlock());
            return;
        }

        if (!BlockUtil.checkVerticalMaterials(BlockUtil.MatchType.ALL_IGNORE_REPLACEABLE, world, pos.up(), treeHeight - 1)) {
            Logger.debug("[SaplingGrowTreeEvent] Aborting RTG tree generation: not enough space above");
            return;
        }

        /*
         * Set the generateFlag to what it needs to be for growing trees from saplings,
         * generate the tree, and then set it back to what it was before.
         *
         * TODO: Does this affect the generation of normal RTG trees? - Pink
         */
        int oldFlag = tree.getGenerateFlag();
        tree.setGenerateFlag(3);
        boolean generated = tree.generate(world, rand, pos);
        tree.setGenerateFlag(oldFlag);

        if (generated) {
            event.setResult(Event.Result.DENY);
            // Sometimes we have to remove the sapling manually because some trees grow around it, leaving the original sapling.
            if (world.getBlockState(pos) == saplingBlock) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
            }
        }
        Logger.trace("Finished handling SaplingGrowTreeEvent in Biome: {}, with sapling: {}", rtgBiome.baseBiomeResLoc(), saplingBlock);
    }


// ORE_GEN_BUS

//TODO: [1.12] Remove the ChunkOreGenTracker and properly resolve extra ore being generated during biome decoration.
    @SubscribeEvent
    public static void onGenerateMinable(OreGenEvent.GenerateMinable event) {

        if (!RTGConfig.allowOreGenEventCancel()) { return; }

        IChunkGenerator chunkGenerator = ((WorldServer) event.getWorld()).getChunkProvider().chunkGenerator;
        if (chunkGenerator instanceof ChunkGeneratorRTG) {

            ChunkOreGenTracker chunkOreGenTracker = ((ChunkGeneratorRTG) chunkGenerator).getChunkOreGenTracker();
            BlockPos eventPos = event.getPos();
            OreGenEvent.GenerateMinable.EventType eventType = event.getType();

            if (eventType != GenerateMinable.EventType.QUARTZ &&
                eventType != GenerateMinable.EventType.CUSTOM &&
                chunkOreGenTracker.hasGeneratedOres(eventPos)) {

                event.setResult(Event.Result.DENY);
            }
        }
    }
}
