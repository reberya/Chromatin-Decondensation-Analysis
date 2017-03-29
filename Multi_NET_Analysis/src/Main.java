import java.io.FileNotFoundException;
/**
 * Opens GUI window from which user can run DANA,
 * save/load settings.
 * 
 * @author Ryan Rebernick
 *
 */
public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		//Opens Window for User
		Window newGui = new Window();
		newGui.setVisible(true);
	}
}
