package frameWork;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class SetupProperties {

		public static Properties webProp() throws FileNotFoundException, IOException {
		Properties prop = new Properties();
		prop.load(new FileInputStream("src/test/resources/app.properties"));
		return prop;
	}

}
