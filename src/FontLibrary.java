import java.io.InputStream;
import java.util.HashMap;
import java.util.MissingResourceException;

import javafx.scene.text.Font;

public class FontLibrary {
	private static final HashMap<String, Font> fontMap = new HashMap<>();
	private static double defaultSize = 12;
	
	public static void addFont(String name, String resourcePath) {
		InputStream stream = FontLibrary.class.getResourceAsStream(resourcePath);

		if(stream != null) {
			Font font = Font.loadFont(stream, defaultSize);

			if(font != null) {
				fontMap.put(name, font);
				System.out.println("Font loaded successfully!");
				System.out.println("  Alias: " + name);
				System.out.println("  Font family name: " + font.getFamily());
				System.out.println("  Font name: " + font.getName());
			}
			else {
				System.err.println("Failed to load font from: " + resourcePath);
				throw new MissingResourceException("Font,", null, null);
			}
		}
		else {
			System.err.println("Font resource not found: " + resourcePath);
		}
	}

	
	public static Font getFont(String name, double size) {
		Font font = fontMap.get(name);
		
		if(font != null) {
			return Font.font(font.getName(), size);
		}
		
		return Font.font("System", size);
	}
}
