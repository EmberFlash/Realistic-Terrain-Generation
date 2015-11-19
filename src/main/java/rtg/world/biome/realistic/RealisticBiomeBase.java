package rtg.world.biome.realistic;

import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.CLAY;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.COAL;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.DIAMOND;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.DIRT;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.GOLD;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.GRAVEL;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.IRON;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.LAPIS;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.REDSTONE;

import java.util.Random;

import rtg.config.rtg.ConfigRTG;
import rtg.util.CellNoise;
import rtg.util.OpenSimplexNoise;
import rtg.world.biome.BiomeBase;
import rtg.world.biome.WorldChunkManagerRTG;
import rtg.world.gen.feature.WorldGenClay;
import rtg.world.gen.surface.SurfaceBase;
import rtg.world.gen.terrain.TerrainBase;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenMinable;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class RealisticBiomeBase extends BiomeBase {
    
    private static final RealisticBiomeBase[] arrRealisticBiomeIds = new RealisticBiomeBase[256];
    
    public final BiomeGenBase baseBiome;
    public final BiomeGenBase riverBiome;
    public String realisticBiomeName = null;
    public BiomeSize biomeSize;
    public int biomeWeight = 10;
    
    public TerrainBase terrain;
    
    public SurfaceBase[] surfaces;
    public int surfacesLength;
    
    public int waterLakeFrequency; //Lower = more frequent
    public int lavaLakeFrequency; //Lower = more frequent
    
    public int clayPerChunk;
    public int dirtPerChunk;
    public int gravelPerChunk;
    public int coalPerChunk;
    public int ironPerChunk;
    public int goldPerChunk;
    public int redstonePerChunk;
    public int diamondPerChunk;
    public int lapisPerChunk;
    
    public RealisticBiomeBase(BiomeGenBase biome) {
    
        this(biome, BiomeBase.climatizedBiome(BiomeGenBase.river, Climate.TEMPERATE));
    }
    
    public RealisticBiomeBase(BiomeGenBase biome, BiomeGenBase river) {
    
        super(biome.biomeID);
        
        arrRealisticBiomeIds[biome.biomeID] = this;
                
        baseBiome = biome;
        riverBiome = river;
        
        waterLakeFrequency = 10;
        lavaLakeFrequency = 0; // Disabled.
        
        clayPerChunk = 20;
        dirtPerChunk = 32;
        gravelPerChunk = 32;
        coalPerChunk = 16;
        ironPerChunk = 8;
        goldPerChunk = 8;
        redstonePerChunk = 7;
        diamondPerChunk = 7;
        lapisPerChunk = 6;
    }
    
    public static RealisticBiomeBase getBiome(int id) {
    
        return arrRealisticBiomeIds[id];
    }
    
    public RealisticBiomeBase(BiomeGenBase b, BiomeGenBase riverbiome, TerrainBase t, SurfaceBase[] s) {
    
        this(b, riverbiome);
        
        terrain = t;
        
        surfaces = s;
        surfacesLength = s.length;
    }
    
    public RealisticBiomeBase(BiomeGenBase b, BiomeGenBase riverbiome, TerrainBase t, SurfaceBase s) {
    
        this(b, riverbiome, t, new SurfaceBase[] {s});
    }
    
    public void rDecorate(World world, Random rand, int chunkX, int chunkY, OpenSimplexNoise simplex, CellNoise cell, float strength, float river) {
    
        if (strength > 0.3f) {
            baseBiome.decorate(world, rand, chunkX, chunkY);
        }
    }
    
    public static void rDecorateSeedBiome(World world, Random rand, int chunkX, int chunkY, OpenSimplexNoise simplex, CellNoise cell, float strength, float river, BiomeGenBase seedBiome) {
        
        if (strength > 0.3f) {
            seedBiome.decorate(world, rand, chunkX, chunkY);
        }
    }
    
    public void generateMapGen(Block[] blocks, byte[] metadata, Long seed, World world, WorldChunkManagerRTG cmr, Random mapRand, int chunkX, int chunkY, OpenSimplexNoise simplex, CellNoise cell, float noise[]) {
    
        int k = 5;
        mapRand.setSeed(seed);
        long l = (mapRand.nextLong() / 2L) * 2L + 1L;
        long l1 = (mapRand.nextLong() / 2L) * 2L + 1L;
        for (int baseX = chunkX - k; baseX <= chunkX + k; baseX++) {
            for (int baseY = chunkY - k; baseY <= chunkY + k; baseY++) {
                mapRand.setSeed((long) baseX * l + (long) baseY * l1 ^ seed);
                rMapGen(blocks, metadata, world, cmr, mapRand, baseX, baseY, chunkX, chunkY, simplex, cell, noise);
            }
        }
    }
    
    public void rMapGen(Block[] blocks, byte[] metadata, World world, WorldChunkManagerRTG cmr, Random mapRand, int chunkX, int chunkY, int baseX, int baseY, OpenSimplexNoise simplex, CellNoise cell, float noise[]) {
    
    }
    
    public float rNoise(OpenSimplexNoise simplex, CellNoise cell, int x, int y, float border, float river) {
    
        return terrain.generateNoise(simplex, cell, x, y, border, river);
    }
    
    public void rReplace(Block[] blocks, byte[] metadata, int i, int j, int x, int y, int depth, World world, Random rand, OpenSimplexNoise simplex, CellNoise cell, float[] noise, float river, BiomeGenBase[] base) {
    
        for (int s = 0; s < surfacesLength; s++) {
            surfaces[s].paintTerrain(blocks, metadata, i, j, x, y, depth, world, rand, simplex, cell, noise, river, base);
        }
    }
    
    public float r3Dnoise(float z) {
    
        return 0f;
    }
    
    public String getRealisticBiomeName() {
    
        return this.realisticBiomeName;
    }
    
    public void setRealisticBiomeName(String n) {
    
        this.realisticBiomeName = n;
    }
    
    public void rGenerateOres(World worldObj, Random rand, int x, int y)
    {
        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(worldObj, rand, x, y));
        
        rGenerateOreDirt(worldObj, rand, x, y);
        rGenerateOreGravel(worldObj, rand, x, y);
        rGenerateOreCoal(worldObj, rand, x, y);
        rGenerateOreIron(worldObj, rand, x, y);
        rGenerateOreGold(worldObj, rand, x, y);
        rGenerateOreRedstone(worldObj, rand, x, y);
        rGenerateOreDiamond(worldObj, rand, x, y);
        rGenerateOreLapis(worldObj, rand, x, y);
        
        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(worldObj, rand, x, y));
    }
    
    public void rGenerateOreDirt(World worldObj, Random rand, int x, int y)
    {
        WorldGenMinable ore_dirt = new WorldGenMinable(Blocks.dirt, dirtPerChunk);
        
        if (TerrainGen.generateOre(worldObj, rand, ore_dirt, x, y, DIRT)) {
            
            for (int j2 = 0; j2 < 10; j2++) {
                
                int l5 = x + rand.nextInt(16);
                int i9 = rand.nextInt(64);
                int l11 = y + rand.nextInt(16);
                
                ore_dirt.generate(worldObj, rand, l5, i9, l11);
            }
        }
    }
    
    public void rGenerateOreGravel(World worldObj, Random rand, int x, int y)
    {
        WorldGenMinable ore_gravel = new WorldGenMinable(Blocks.gravel, gravelPerChunk);
        
        if (TerrainGen.generateOre(worldObj, rand, ore_gravel, x, y, GRAVEL)) {
            
            for (int k2 = 0; k2 < 5; k2++) {
                
                int i6 = x + rand.nextInt(16);
                int j9 = rand.nextInt(64);
                int i12 = y + rand.nextInt(16);
                
                ore_gravel.generate(worldObj, rand, i6, j9, i12);
            }
        }
    }
    
    public void rGenerateOreCoal(World worldObj, Random rand, int x, int y)
    {
        WorldGenMinable ore_coal = new WorldGenMinable(Blocks.coal_ore, coalPerChunk);
        
        if (ConfigRTG.generateOreCoal && TerrainGen.generateOre(worldObj, rand, ore_coal, x, y, COAL)) {
            
            for (int i3 = 0; i3 < 20; i3++) {
                
                int j6 = x + rand.nextInt(16);
                int k9 = rand.nextInt(128);
                int j12 = y + rand.nextInt(16);
                
                ore_coal.generate(worldObj, rand, j6, k9, j12);
            }
        }
    }
    
    public void rGenerateOreIron(World worldObj, Random rand, int x, int y)
    {
        WorldGenMinable ore_iron = new WorldGenMinable(Blocks.iron_ore, ironPerChunk);
        
        if (ConfigRTG.generateOreIron && TerrainGen.generateOre(worldObj, rand, ore_iron, x, y, IRON)) {
            
            for (int j3 = 0; j3 < 20; j3++) {
                
                int k6 = x + rand.nextInt(16);
                int l9 = rand.nextInt(64);
                int k12 = y + rand.nextInt(16);
                
                ore_iron.generate(worldObj, rand, k6, l9, k12);
            }
        }
    }
    
    public void rGenerateOreGold(World worldObj, Random rand, int x, int y)
    {
        WorldGenMinable ore_gold = new WorldGenMinable(Blocks.gold_ore, goldPerChunk);
        
        if (ConfigRTG.generateOreGold && TerrainGen.generateOre(worldObj, rand, ore_gold, x, y, GOLD)) {
            
            for (int k3 = 0; k3 < 2; k3++) {
                
                int l6 = x + rand.nextInt(16);
                int i10 = rand.nextInt(32);
                int l12 = y + rand.nextInt(16);
                
                ore_gold.generate(worldObj, rand, l6, i10, l12);
            }
        }
    }
    
    public void rGenerateOreRedstone(World worldObj, Random rand, int x, int y)
    {
        WorldGenMinable ore_redstone = new WorldGenMinable(Blocks.redstone_ore, redstonePerChunk);
        
        if (ConfigRTG.generateOreRedstone && TerrainGen.generateOre(worldObj, rand, ore_redstone, x, y, REDSTONE)) {
            
            for (int l3 = 0; l3 < 8; l3++) {
                
                int i7 = x + rand.nextInt(16);
                int j10 = rand.nextInt(16);
                int i13 = y + rand.nextInt(16);
                
                ore_redstone.generate(worldObj, rand, i7, j10, i13);
            }
        }
    }
    
    public void rGenerateOreDiamond(World worldObj, Random rand, int x, int y)
    {
        WorldGenMinable ore_diamond = new WorldGenMinable(Blocks.diamond_ore, diamondPerChunk);
        
        if (ConfigRTG.generateOreDiamond && TerrainGen.generateOre(worldObj, rand, ore_diamond, x, y, DIAMOND)) {
            
            for (int i4 = 0; i4 < 1; i4++) {
                
                int j7 = x + rand.nextInt(16);
                int k10 = rand.nextInt(16);
                int j13 = y + rand.nextInt(16);
                
                ore_diamond.generate(worldObj, rand, j7, k10, j13);
            }
        }
    }
    
    public void rGenerateOreLapis(World worldObj, Random rand, int x, int y)
    {
        WorldGenMinable ore_lapis = new WorldGenMinable(Blocks.lapis_ore, lapisPerChunk);
        
        if (ConfigRTG.generateOreLapis && TerrainGen.generateOre(worldObj, rand, ore_lapis, x, y, LAPIS)) {
            
            for (int j4 = 0; j4 < 1; j4++) {
                
                int k7 = x + rand.nextInt(16);
                int l10 = rand.nextInt(16) + rand.nextInt(16);
                int k13 = y + rand.nextInt(16);
                
                ore_lapis.generate(worldObj, rand, k7, l10, k13);
            }
        }
    }
    
    public void rDecorateClay(World worldObj, Random rand, int i, int j, float river, int x, int y)
    {
        if (TerrainGen.decorate(worldObj, rand, i, j, CLAY)) {
            
            if (river > 0.85f) {
                
                for (int j2 = 0; j2 < 3; j2++) {
                    
                    int l5 = x + rand.nextInt(16);
                    int i9 = 53 + rand.nextInt(15);
                    int l11 = y + rand.nextInt(16);
                    
                    (new WorldGenClay(Blocks.clay, 0, clayPerChunk)).generate(worldObj, rand, l5, i9, l11);
                }
            }
        }
    }
}
