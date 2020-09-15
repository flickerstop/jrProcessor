package scripts;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.tribot.api.General;
import org.tribot.api.util.Screenshots;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Starting;

import scripts.util.BreakManager;
import scripts.util.Items;
import scripts.util.Network;
import scripts.util.Util;
// Test
import scripts.util.Walk;

@ScriptManifest(authors = { "JR" }, category = "Tools", name = "JR TESTER")
public class test extends Script implements Starting{


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		General.println(1);
	
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			ImageIO.write(Screenshots.getScreenshotImage(), "png", output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		General.println("data:image/png;base64," + DatatypeConverter.printBase64Binary(output.toByteArray()));
	}

	

}
	
	

