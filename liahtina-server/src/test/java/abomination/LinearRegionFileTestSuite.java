package abomination;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class LinearRegionFileTestSuite {

    @Test
    public void testLinearV2AndV3Formats(@TempDir Path tempDir) throws Exception {
        // Test 1: Create a V2 file, write 2 chunks in different buckets
        Path v2Path = tempDir.resolve("r.0.0.linear");
        RegionStorageInfo info = new RegionStorageInfo("world", null, "chunk");

        LinearRegionFile v2File = new LinearRegionFile(info, v2Path, tempDir, false, 1, LinearRegionFile.VERSION_V2);
        byte[] chunk0Data = new byte[100];
        Arrays.fill(chunk0Data, (byte) 42);
        // Chunk (0, 0) belongs to bucket 0
        v2File.write(new ChunkPos(0, 0), ByteBuffer.wrap(chunk0Data));

        byte[] chunk16Data = new byte[200];
        Arrays.fill(chunk16Data, (byte) 84);
        // Chunk (16, 16) belongs to a different bucket
        v2File.write(new ChunkPos(16, 16), ByteBuffer.wrap(chunk16Data));

        v2File.flush();
        v2File.close();

        // Verify V2 file header
        byte[] v2Bytes = Files.readAllBytes(v2Path);
        ByteBuffer v2Buffer = ByteBuffer.wrap(v2Bytes);
        long superblock = v2Buffer.getLong();
        Assertions.assertEquals(0xc3ff13183cca9d9aL, superblock);
        byte version = v2Buffer.get();
        Assertions.assertEquals(3, version, "V2 header version should be 3");

        // Verify bitmap bug fix:
        // Open the file again in V2 mode, but ONLY access/write to bucket containing (0, 0)
        // Do NOT touch chunk (16, 16)
        LinearRegionFile v2Reopened = new LinearRegionFile(info, v2Path, tempDir, false, 1, LinearRegionFile.VERSION_V2);
        byte[] newChunk0Data = new byte[150];
        Arrays.fill(newChunk0Data, (byte) 99);
        v2Reopened.write(new ChunkPos(0, 0), ByteBuffer.wrap(newChunk0Data));
        v2Reopened.flush();
        v2Reopened.close();

        // Check the newly written V2 file: chunk (16, 16) in unopened bucket MUST still be marked in bitmap!
        byte[] v2ReopenedBytes = Files.readAllBytes(v2Path);
        ByteBuffer v2CheckBuf = ByteBuffer.wrap(v2ReopenedBytes);
        v2CheckBuf.getLong(); // superblock
        v2CheckBuf.get(); // version
        v2CheckBuf.getLong(); // timestamp
        v2CheckBuf.get(); // gridSize
        v2CheckBuf.getInt(); // regionX
        v2CheckBuf.getInt(); // regionZ

        // Read bitmap (128 bytes = 1024 bits)
        byte[] bitmap = new byte[128];
        v2CheckBuf.get(bitmap);

        int chunk0Index = 0; // (0, 0)
        int chunk16Index = 16 + 16 * 32; // (16, 16) = 528

        boolean chunk0Bit = ((bitmap[chunk0Index / 8] >> (7 - (chunk0Index % 8))) & 1) == 1;
        boolean chunk16Bit = ((bitmap[chunk16Index / 8] >> (7 - (chunk16Index % 8))) & 1) == 1;

        Assertions.assertTrue(chunk0Bit, "Chunk (0, 0) must be marked in bitmap");
        Assertions.assertTrue(chunk16Bit, "Chunk (16, 16) from unopened bucket must STILL be marked in bitmap (fixed bug)");

        // Verify reading both chunks
        LinearRegionFile v2Verify = new LinearRegionFile(info, v2Path, tempDir, false, 1, LinearRegionFile.VERSION_V2);
        try (DataInputStream in0 = v2Verify.getChunkDataInputStream(new ChunkPos(0, 0))) {
            Assertions.assertNotNull(in0);
            byte[] read0 = in0.readAllBytes();
            Assertions.assertArrayEquals(newChunk0Data, read0);
        }
        try (DataInputStream in16 = v2Verify.getChunkDataInputStream(new ChunkPos(16, 16))) {
            Assertions.assertNotNull(in16);
            byte[] read16 = in16.readAllBytes();
            Assertions.assertArrayEquals(chunk16Data, read16);
        }
        v2Verify.close();

        // Test 2: Create a V3 file
        Path v3Path = tempDir.resolve("r.1.1.linear");
        LinearRegionFile v3File = new LinearRegionFile(info, v3Path, tempDir, false, 1, LinearRegionFile.VERSION_V3);
        byte[] v3ChunkData = new byte[300];
        Arrays.fill(v3ChunkData, (byte) 77);
        v3File.write(new ChunkPos(1, 2), ByteBuffer.wrap(v3ChunkData));
        v3File.flush();
        v3File.close();

        // Verify V3 file header
        byte[] v3Bytes = Files.readAllBytes(v3Path);
        ByteBuffer v3Buffer = ByteBuffer.wrap(v3Bytes);
        long v3Superblock = v3Buffer.getLong();
        Assertions.assertEquals(0xc3ff13183cca9d9aL, v3Superblock);
        byte v3Version = v3Buffer.get();
        Assertions.assertEquals(4, v3Version, "V3 header version should be 4");

        // Read V3 file and verify data
        LinearRegionFile v3Read = new LinearRegionFile(info, v3Path, tempDir, false, 1, LinearRegionFile.VERSION_V3);
        try (DataInputStream inV3 = v3Read.getChunkDataInputStream(new ChunkPos(1, 2))) {
            Assertions.assertNotNull(inV3);
            byte[] readV3 = inV3.readAllBytes();
            Assertions.assertArrayEquals(v3ChunkData, readV3);
        }
        v3Read.close();

        // Test 3: Cross-compatibility
        // Read V2 file using V3 instance
        LinearRegionFile crossReadV2 = new LinearRegionFile(info, v2Path, tempDir, false, 1, LinearRegionFile.VERSION_V3);
        try (DataInputStream inCross = crossReadV2.getChunkDataInputStream(new ChunkPos(16, 16))) {
            Assertions.assertNotNull(inCross);
            Assertions.assertArrayEquals(chunk16Data, inCross.readAllBytes());
        }
        crossReadV2.close();

        // Read V3 file using V2 instance
        LinearRegionFile crossReadV3 = new LinearRegionFile(info, v3Path, tempDir, false, 1, LinearRegionFile.VERSION_V2);
        try (DataInputStream inCross = crossReadV3.getChunkDataInputStream(new ChunkPos(1, 2))) {
            Assertions.assertNotNull(inCross);
            Assertions.assertArrayEquals(v3ChunkData, inCross.readAllBytes());
        }
        crossReadV3.close();
    }
}
