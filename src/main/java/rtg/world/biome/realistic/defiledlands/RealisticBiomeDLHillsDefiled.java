package rtg.world.biome.realistic.defiledlands;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.util.WorldUtil;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.HeightEffect;
import rtg.api.world.terrain.heighteffect.JitterEffect;
import rtg.api.world.terrain.heighteffect.MountainsWithPassesEffect;
import rtg.world.biome.realistic.vanilla.RealisticBiomeVanillaExtremeHills;


public class RealisticBiomeDLHillsDefiled extends RealisticBiomeDLBase {

    public RealisticBiomeDLHillsDefiled(Biome biome) {

        super(biome);
    }

    @Override
    public void initConfig() {
        this.getConfig().ALLOW_RIVERS.set(false);
        this.getConfig().ALLOW_SCENIC_LAKES.set(false);
        this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
    }

    @Override
    public TerrainBase initTerrain() {

        return new RealisticBiomeVanillaExtremeHills.RidgedExtremeHills(150f, 67f, 200f);
    }

    @Override
    public SurfaceBase initSurface() {

        return new SurfaceVanillaExtremeHillsPlus(getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, 0f, 1.5f, 60f, 65f, 1.5f, BlockUtil.getBlock(new ResourceLocation("defiledlands:stone_defiled")).getDefaultState(), 0.08f);
    }

    public static class TerrainVanillaExtremeHillsPlus extends TerrainBase {

        private float width;
        private float strength;
        private float spikeWidth = 40;
        private float spikeHeight = 70;
        private HeightEffect heightEffect;

        public TerrainVanillaExtremeHillsPlus(float mountainWidth, float mountainStrength, float height) {

            width = mountainWidth;
            strength = mountainStrength;
            base = height;
            MountainsWithPassesEffect mountainEffect = new MountainsWithPassesEffect();
            mountainEffect.mountainHeight = strength;
            mountainEffect.mountainWavelength = width;
            mountainEffect.spikeHeight = this.spikeHeight;
            mountainEffect.spikeWavelength = this.spikeWidth;

            heightEffect = new JitterEffect(7f, 10f, mountainEffect);
            heightEffect = new JitterEffect(3f, 6f, heightEffect);
            //this(mountainWidth, mountainStrength, depthLake, 260f, 68f);
        }

        @Override
        public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {

            return riverized(heightEffect.added(rtgWorld, x, y) + base, river);
        }
    }

    public static class SurfaceVanillaExtremeHillsPlus extends SurfaceBase {

        private float min;

        private float sCliff = 1.5f;
        private float sHeight = 60f;
        private float sStrength = 65f;
        private float cCliff = 1.5f;

        private IBlockState mixBlock;
        private float mixHeight;

        private IBlockState defiledStone = BlockUtil.getBlock(new ResourceLocation("defiledlands:stone_defiled")).getDefaultState();

        public SurfaceVanillaExtremeHillsPlus(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff,
                                              float stoneHeight, float stoneStrength, float clayCliff, IBlockState mix, float mixSize) {

            super(config, top, fill);
            min = minCliff;

            sCliff = stoneCliff;
            sHeight = stoneHeight;
            sStrength = stoneStrength;
            cCliff = clayCliff;

            mixBlock = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), mix);
            mixHeight = mixSize;
        }

        @Override
        protected IBlockState hcStone(RTGWorld rtgWorld, int i, int j, int x, int y, int k) {

            return this.topBlock;
        }

        @Override
        protected IBlockState hcCobble(RTGWorld rtgWorld, int worldX, int worldZ, int chunkX, int chunkZ, int worldY) {

            return this.topBlock;
        }

        @Override
        protected IBlockState getShadowStoneBlock(RTGWorld rtgWorld, int i, int j, int x, int y, int k) {

            return defiledStone;
        }

        @Override
        public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {

            Random rand = rtgWorld.rand();
            SimplexNoise simplex = rtgWorld.simplexInstance(0);
            float c = WorldUtil.Terrain.calcCliff(x, z, noise);
            int cliff = 0;
            boolean m = false;

            Block b;
            for (int k = 255; k > -1; k--) {
                b = primer.getBlockState(x, k, z).getBlock();
                if (b == Blocks.AIR) {
                    depth = -1;
                }
                else if (b == Blocks.STONE) {
                    depth++;

                    if (depth == 0) {

                        float p = simplex.noise3f(i / 8f, j / 8f, k / 8f) * 0.5f;
                        if (c > min && c > sCliff - ((k - sHeight) / sStrength) + p) {
                            cliff = 1;
                        }
                        if (c > cCliff) {
                            cliff = 2;
                        }

                        if (cliff == 1) {
                            if (rand.nextInt(3) == 0) {

                                primer.setBlockState(x, k, z, hcCobble(rtgWorld, i, j, x, z, k));
                            }
                            else {

                                primer.setBlockState(x, k, z, hcStone(rtgWorld, i, j, x, z, k));
                            }
                        }
                        else if (cliff == 2) {
                            primer.setBlockState(x, k, z, getShadowStoneBlock(rtgWorld, i, j, x, z, k));
                        }
                        else if (k < 63) {
                            if (k < 62) {
                                primer.setBlockState(x, k, z, fillerBlock);
                            }
                            else {
                                primer.setBlockState(x, k, z, topBlock);
                            }
                        }
                        else if (simplex.noise2f(i / 12f, j / 12f) > mixHeight) {
                            primer.setBlockState(x, k, z, mixBlock);
                            m = true;
                        }
                        else {
                            primer.setBlockState(x, k, z, topBlock);
                        }
                    }
                    else if (depth < 6) {
                        if (cliff == 1) {
                            primer.setBlockState(x, k, z, hcStone(rtgWorld, i, j, x, z, k));
                        }
                        else if (cliff == 2) {
                            primer.setBlockState(x, k, z, getShadowStoneBlock(rtgWorld, i, j, x, z, k));
                        }
                        else {
                            primer.setBlockState(x, k, z, fillerBlock);
                        }
                    }
                }
            }
        }
    }
}
