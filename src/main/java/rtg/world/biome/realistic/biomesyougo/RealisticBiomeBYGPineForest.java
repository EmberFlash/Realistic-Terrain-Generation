package rtg.world.biome.realistic.biomesyougo;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;


public class RealisticBiomeBYGPineForest extends RealisticBiomeBYGBase {

    public RealisticBiomeBYGPineForest(Biome biome) {

        super(biome);
    }

    @Override
    public void initConfig() {

    }

    @Override
    public TerrainBase initTerrain() {

        return new TerrainBOPSnowyForest(58f, 69f, 28f);
    }

    public static class TerrainBOPSnowyForest extends TerrainBase {

        private float minHeight;
        private float maxHeight;
        private float hillStrength;

        public TerrainBOPSnowyForest(float minHeight, float maxHeight, float hillStrength) {

            this.minHeight = minHeight;
            this.maxHeight = (maxHeight > rollingHillsMaxHeight) ? rollingHillsMaxHeight : ((maxHeight < this.minHeight) ? rollingHillsMaxHeight : maxHeight);
            this.hillStrength = hillStrength;
        }

        @Override
        public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {

            return terrainRollingHills(x, y, rtgWorld, river, hillStrength, maxHeight, groundNoiseAmplitudeHills, 4f);
        }
    }
}
