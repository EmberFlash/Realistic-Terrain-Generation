package rtg.api.world.gen.feature;

import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;


public class WorldGenTallGrassRTG extends WorldGenerator {

    private IBlockState block;

    public WorldGenTallGrassRTG(BlockTallGrass.EnumType type) {
        this.block = Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, type);
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        while (y > 0) {
            if (!world.isAirBlock(new BlockPos(x, y, z)) || world.getBlockState(new BlockPos(x, y, z)).getBlock().isLeaves(world.getBlockState(new BlockPos(x, y, z)), world, new BlockPos(x, y, z))) {
                break;
            }
            y--;
        }

        //for (int l = 0; l < 128; ++l)
        {
            int i1 = x;// + rand.nextInt(8) - rand.nextInt(8);
            int j1 = y + rand.nextInt(4) - rand.nextInt(4);
            int k1 = z;// + rand.nextInt(8) - rand.nextInt(8);

            if (world.isAirBlock(new BlockPos(i1, j1, k1)) && block.getBlock().canPlaceBlockAt(world, new BlockPos(i1, j1, k1))) {
                world.setBlockState(new BlockPos(i1, j1, k1), block, 2);
            }
        }

        return true;
    }
}