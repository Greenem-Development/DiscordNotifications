package me.greenem.DiscordNotifications;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class PluginPart extends JavaPlugin implements Listener {
	
	Logger log = getLogger();
	final Random random = new Random(System.currentTimeMillis());
	public Properties props = new Properties();
	public boolean Online;
	
	@Override
	public void onDisable() {
		log.info(ChatColor.RED + "DiscordNotifications plugin has been disabled.");
		Online = false;
		SaveServerData();
	}

	@Override
	public void onEnable() {
		log.info(ChatColor.GREEN + "DiscordNotifications plugin has been enabled!");
		getServer().getPluginManager().registerEvents(this, this);
		Online = true;
		SaveServerData();
	}
	
	public void SaveServerData() {
		String Description = getServerData("motd");
		String Name = getServerName(Description);
		String State = null;
		String Port = getServerData("server-port");
		String VersionAndCore = getServer().getVersion().toString();
		if(VersionAndCore.toLowerCase().contains("spigot")) VersionAndCore = "Spigot";
		if(VersionAndCore.toLowerCase().contains("paper")) VersionAndCore = "Paper";
		if(VersionAndCore.toLowerCase().contains("bukkit")) VersionAndCore = "Bukkit";
		if(VersionAndCore.toLowerCase().contains("craftbukkit")) VersionAndCore = "CraftBukkit";
		String McVersion = GetMcVersion();
		boolean HasAvatar = true;
		if(Online == true) {
			State = "Online";
		}
		else {
			State = "Offline";
		}
		String Time = getTime("HH:mm");
		FileReader reader = null;
		props.clear();
		File f = new File("G:\\FishBot\\minecraft\\mcServerInfo.properties");
		reader = null;
		props.clear();
		if (f.exists()) {
			try {
				reader = new FileReader(f);
				try {
					props.load(reader);
					reader.close();
					try{
						Image image = ImageIO.read(new File(getServer().getWorldContainer().getCanonicalPath() + "\\server-icon.png"));	
						BufferedImage originalImage = (BufferedImage) image;
						BufferedImage result = resizeImage(originalImage , 128, 128);
						File outputfile = new File("G:\\FishBot\\minecraft\\icon.png");
						ImageIO.write(result, "png", outputfile);
					}
					catch(javax.imageio.IIOException err) {
						HasAvatar = false;
					}
					if(HasAvatar == true) {
						props.setProperty("avatar", "true");
					}
					else {
						props.setProperty("avatar", "false");
					}
					props.setProperty("name", Name);
					props.setProperty("description", Description);
					props.setProperty("state", State);
					props.setProperty("time", Time);
					props.setProperty("server-port", Port);
					props.setProperty("server-version", VersionAndCore);
					props.setProperty("mc-version", McVersion);
					FileOutputStream fos = new FileOutputStream(f);
					props.store(fos, "");
					fos.close();
					Runtime.getRuntime().exec("G:\\FishBot\\minecraft\\runbot.bat", null, new File("G:\\FishBot\\minecraft\\"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public String getServerName(String motd) {
		if(motd.toLowerCase().contains("server 7")) return "Greenem SMP";
		if(motd.toLowerCase().contains("server 6")) return "Moscow building server";
		//if(motd.toLowerCase().contains("server 5")) return "MH/DS server";
		if(motd.toLowerCase().contains("server 4")) return "Greenem's official server";
		if(motd.toLowerCase().contains("server 3")) return "MH/DS server";
		if(motd.toLowerCase().contains("server 2")) return "Maps server";
		if(motd.toLowerCase().contains("server 1")) return "MH/DS server";
		
		return motd;
	}
	
	public String getServerData(String name) {
		FileReader reader = null;
		props.clear();
		File f = null;
		try {
			f = new File(getServer().getWorldContainer().getCanonicalPath() + "\\server.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (f.exists()) {
			try {
				reader = new FileReader(f);
				try {
					props.load(reader);
					reader.close();
					
					String result = props.getProperty(name);
					return result;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	public String getTime(String format) {
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		DateFormat dateformat = new SimpleDateFormat(format);
		String result = dateformat.format(date);
		return result;	
	}
	
	public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
	    Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
	    BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
	    outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
	    return outputImage;
	}

	public static void copyFileUsingStream(File source, File dest) throws IOException {
    InputStream is = null;
    OutputStream os = null;
    try {
        is = new FileInputStream(source);
        os = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
    } finally {
        is.close();
        os.close();
    }
}

	public String GetMcVersion() {
		String[] serverVersion = Bukkit.getBukkitVersion().split("-");
		String version = serverVersion[0];
		return version;
	}
}