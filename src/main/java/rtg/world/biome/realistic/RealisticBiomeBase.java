package rtg.world.biome.realistic;

import java.util.Random;

import rtg.util.CellNoise;
import rtg.util.PerlinNoise;
import rtg.world.biome.BiomeBase;
import rtg.world.biome.ChunkManagerRealistic;
import rtg.world.biome.BiomeBase.Climate;
import rtg.world.biome.realistic.vanilla.*;
import rtg.world.gen.surface.SurfaceBase;
import rtg.world.gen.terrain.TerrainBase;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class RealisticBiomeBase
{
	private static final RealisticBiomeBase[] biomeList = new RealisticBiomeBase[256];
	private static int nextBiomeID = 0;

	//VANILLA =======================================================================================
	public static RealisticBiomeBase vanillaBeach = new RealisticBiomeVanillaBeach();
	public static RealisticBiomeBase vanillaBirchForest = new RealisticBiomeVanillaBirchForest();
	public static RealisticBiomeBase vanillaBirchForestHills = new RealisticBiomeVanillaBirchForestHills();
	public static RealisticBiomeBase vanillaColdBeach = new RealisticBiomeVanillaColdBeach();
	public static RealisticBiomeBase vanillaColdTaiga = new RealisticBiomeVanillaColdTaiga();
	public static RealisticBiomeBase vanillaColdTaigaHills = new RealisticBiomeVanillaColdTaigaHills();
	public static RealisticBiomeBase vanillaDeepOcean = new RealisticBiomeVanillaDeepOcean();
	public static RealisticBiomeBase vanillaDesert = new RealisticBiomeVanillaDesert();
	public static RealisticBiomeBase vanillaDesertHills = new RealisticBiomeVanillaDesertHills();
	public static RealisticBiomeBase vanillaExtremeHills = new RealisticBiomeVanillaExtremeHills();
	public static RealisticBiomeBase vanillaExtremeHillsPlus = new RealisticBiomeVanillaExtremeHillsPlus();
	public static RealisticBiomeBase vanillaForest = new RealisticBiomeVanillaForest();
	public static RealisticBiomeBase vanillaForestHills = new RealisticBiomeVanillaForestHills();
	public static RealisticBiomeBase vanillaFrozenRiver = new RealisticBiomeVanillaFrozenRiver();
	public static RealisticBiomeBase vanillaIcePlains = new RealisticBiomeVanillaIcePlains();
	public static RealisticBiomeBase vanillaIceMountains = new RealisticBiomeVanillaIceMountains();
	public static RealisticBiomeBase vanillaJungle = new RealisticBiomeVanillaJungle();
	public static RealisticBiomeBase vanillaJungleEdge = new RealisticBiomeVanillaJungleEdge();
	public static RealisticBiomeBase vanillaJungleHills = new RealisticBiomeVanillaJungleHills();
	public static RealisticBiomeBase vanillaMegaTaiga = new RealisticBiomeVanillaMegaTaiga();
	public static RealisticBiomeBase vanillaMegaTaigaHills = new RealisticBiomeVanillaMegaTaigaHills();
	public static RealisticBiomeBase vanillaMesa = new RealisticBiomeVanillaMesa();
	public static RealisticBiomeBase vanillaMesaPlateau = new RealisticBiomeVanillaMesaPlateau();
	public static RealisticBiomeBase vanillaMesaPlateau_F = new RealisticBiomeVanillaMesaPlateauF();
	public static RealisticBiomeBase vanillaMushroomIsland = new RealisticBiomeVanillaMushroomIsland();
	public static RealisticBiomeBase vanillaMushroomIslandShore = new RealisticBiomeVanillaMushroomIslandShore();
	public static RealisticBiomeBase vanillaOcean = new RealisticBiomeVanillaOcean();
	public static RealisticBiomeBase vanillaPlains = new RealisticBiomeVanillaPlains();
	public static RealisticBiomeBase vanillaRiver = new RealisticBiomeVanillaRiver();
	public static RealisticBiomeBase vanillaRoofedForest = new RealisticBiomeVanillaRoofedForest();
	public static RealisticBiomeBase vanillaSavanna = new RealisticBiomeVanillaSavanna();
	public static RealisticBiomeBase vanillaSavannaPlateau = new RealisticBiomeVanillaSavannaPlateau();
	public static RealisticBiomeBase vanillaStoneBeach = new RealisticBiomeVanillaStoneBeach();
	public static RealisticBiomeBase vanillaSwampland = new RealisticBiomeVanillaSwampland();
	public static RealisticBiomeBase vanillaTaiga = new RealisticBiomeVanillaTaiga();
	public static RealisticBiomeBase vanillaTaigaHills = new RealisticBiomeVanillaTaigaHills();
	
	// ==============================================================================================
	
	public final int biomeID;
	public final BiomeGenBase baseBiome;
	public final BiomeGenBase riverBiome;
	
	public TerrainBase terrain;
	
	public SurfaceBase[] surfaces;
	public int surfacesLength;
		
	public RealisticBiomeBase(BiomeGenBase biome)
	{
		this(biome, BiomeBase.climatizedBiome(BiomeGenBase.river, BiomeBase.Climate.TEMPERATE));
	}
	
	public RealisticBiomeBase(BiomeGenBase biome, BiomeGenBase river)
	{
		biomeID = nextBiomeID;
		biomeList[biomeID] = this;
		nextBiomeID++;
		
		baseBiome = biome;
		riverBiome = river;
	}
	
	public static RealisticBiomeBase getBiome(int id)
	{
		return biomeList[id];
	}
	
	
	
	
	
	
	
	
	
	public RealisticBiomeBase(BiomeGenBase b, BiomeGenBase riverbiome, TerrainBase t, SurfaceBase[] s)
	{
		this(b, riverbiome);

		terrain = t;
		
		surfaces = s;
		surfacesLength = s.length;
	}
	
	public RealisticBiomeBase(BiomeGenBase b, BiomeGenBase riverbiome, TerrainBase t, SurfaceBase s)
	{
		this(b, riverbiome, t, new SurfaceBase[]{s});
	}
	
	
	
	
	
	//======================================================================================================================================
	
	
    public void rDecorate(World world, Random rand, int chunkX, int chunkY, PerlinNoise perlin, CellNoise cell, float strength, float river)
    {
    	if(strength > 0.3f)
    	{
    		baseBiome.decorate(world, rand, chunkX, chunkY);
    	}
    }
    
    public void generateMapGen(Block[] blocks, byte[] metadata, Long seed, World world, ChunkManagerRealistic cmr, Random mapRand, int chunkX, int chunkY, PerlinNoise perlin, CellNoise cell, float noise[])
    {
        int k = 5;
        mapRand.setSeed(seed);
        long l = (mapRand.nextLong() / 2L) * 2L + 1L;
        long l1 = (mapRand.nextLong() / 2L) * 2L + 1L;
        for(int baseX = chunkX - k; baseX <= chunkX + k; baseX++)
        {
            for(int baseY = chunkY - k; baseY <= chunkY + k; baseY++)
            {
            	mapRand.setSeed((long)baseX * l + (long)baseY * l1 ^ seed);
                rMapGen(blocks, metadata, world, cmr, mapRand, baseX, baseY, chunkX, chunkY, perlin, cell, noise);
            }
        }
    }
    
    public void rMapGen(Block[] blocks, byte[] metadata, World world, ChunkManagerRealistic cmr, Random mapRand, int chunkX, int chunkY, int baseX, int baseY, PerlinNoise perlin, CellNoise cell, float noise[])
    {
    }
    
    public float rNoise(PerlinNoise perlin, CellNoise cell, int x, int y, float ocean, float border, float river)
    {
		return terrain.generateNoise(perlin, cell, x, y, ocean, border, river);
    }
    
    public void rReplace(Block[] blocks, byte[] metadata, int i, int j, int x, int y, int depth, World world, Random rand, PerlinNoise perlin, CellNoise cell, float[] noise, float river, BiomeGenBase[] base)
    {
    	for(int s = 0; s < surfacesLength; s++)
    	{
    		surfaces[s].paintTerrain(blocks, metadata, i, j, x, y, depth, world, rand, perlin, cell, noise, river, base);
    	}
    }
    
    public float r3Dnoise(float z)
    {
    	return 0f;
    }
}
