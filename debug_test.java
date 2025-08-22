import io.afero.sdk.utils.HexUtils;

public class debug_test {
    public static void main(String[] args) {
        try {
            byte[] result = HexUtils.parseHexBinary("DEADBEEF");
            System.out.println("Successfully parsed DEADBEEF, length: " + result.length);
            for (byte b : result) {
                System.out.printf("0x%02X ", b);
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("Failed to parse DEADBEEF: " + e.getMessage());
        }
    }
}
