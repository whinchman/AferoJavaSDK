import java.io.InputStream;

public class DebugResourceTest {
    public static void main(String[] args) {
        String pathPrefix = "deviceCollection/";
        String path = "getDeviceProfile/bogus-profile-id.json";
        String fullPath = pathPrefix + path;
        
        System.out.println("Trying to load: " + fullPath);
        InputStream is = DebugResourceTest.class.getClassLoader().getResourceAsStream(fullPath);
        System.out.println("InputStream is null: " + (is == null));
        
        // Now try the ones that should exist
        String[] existingPaths = {
            "deviceCollection/getDeviceProfile/profile-001.json",
            "deviceCollection/getDeviceProfile/profile-003.json"
        };
        
        for (String testPath : existingPaths) {
            System.out.println("Trying to load: " + testPath);
            InputStream testIs = DebugResourceTest.class.getClassLoader().getResourceAsStream(testPath);
            System.out.println("InputStream is null: " + (testIs == null));
        }
    }
}
