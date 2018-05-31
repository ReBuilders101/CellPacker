package dev.lb.cellpacker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;


public final class Utils {
	private Utils(){}
	
	public static <T extends JComponent> T setWidth(T control, int width){
		control.setPreferredSize(new Dimension(width, control.getPreferredSize().height));
		return control;
	}
	
	public static <T extends JComponent> T setMaxWidth(T control){
		control.setMaximumSize(new Dimension(Integer.MAX_VALUE, control.getMaximumSize().height));
		return control;
	}
	
	public static <T> T call(T object, Consumer<T> action){
		action.accept(object);
		return object;
	}
	
	public static <T extends JComponent> T setHeight(T control, int height){
		control.setPreferredSize(new Dimension(control.getPreferredSize().width, height));
		return control;
	}
	
	public static <T extends JComponent> T setPrefSize(T control, int width, int height){
		control.setPreferredSize(new Dimension(width, height));
		return control;
	}
	
	public static JPanel createGroupBox(String title){
		JPanel pan = new JPanel();
		pan.setBorder(BorderFactory.createTitledBorder(title));
		return pan;
	}
	
	public static JPanel createGroupBox(String title, LayoutManager layout){
		JPanel pan = new JPanel(layout);
		pan.setBorder(BorderFactory.createTitledBorder(title));
		return pan;
	}
	
	public static <T extends Container> T addAll(T container, Component...components){
		for(Component c : components){
			container.add(c);
		}
		return container;
	}
	
	public static JTextArea getTextDisplay(String text){
		JTextArea txt = new JTextArea(text);
		txt.setEditable(false);
		return txt;
	}
	
	public static JProgressBar getWaitingBar(int width){
		JProgressBar pro = new JProgressBar();
		pro.setIndeterminate(true);
		return setWidth(pro, width);
	}
	
	public static Container asyncFill(Supplier<Component> content, int waitBarWidth){
		JPanel con = new JPanel();
		JProgressBar wait = getWaitingBar(waitBarWidth);
		con.add(wait);
		new Thread(() -> {
			Component com = content.get();
			con.remove(wait);
			con.setLayout(new BorderLayout());
			con.add(com);
			con.revalidate();
		}).start();
		return con;
	}
	
	public static Container asyncFill(Supplier<Component> content, int waitBarWidth, Object constraints){
		JPanel con = new JPanel();
		JProgressBar wait = getWaitingBar(waitBarWidth);
		con.add(wait, constraints);
		new Thread(() -> {
			Component com = content.get();
			con.remove(wait);
			con.setLayout(new BorderLayout());
			con.add(com, constraints);
			con.revalidate();
		}).start();
		return con;
	}
	
	public static JPanel pack(Component...components){
		JPanel container = new JPanel(new FlowLayout());
		for(Component c : components){
			container.add(c);
		}
		return container;
	}
	
	
	//Validation methods from SO
	public static boolean isJsonValid(final String json) {
	    return isJsonValid(new StringReader(json));
	}

	private static boolean isJsonValid(final Reader reader) {
	    return isJsonValid(new JsonReader(reader));
	}

	private static boolean isJsonValid(final JsonReader jsonReader) {
	    try {
	        JsonToken token;
	        loop:
	        while ( (token = jsonReader.peek()) != JsonToken.END_DOCUMENT && token != null ) {
	            switch ( token ) {
	            case BEGIN_ARRAY:
	                jsonReader.beginArray();
	                break;
	            case END_ARRAY:
	                jsonReader.endArray();
	                break;
	            case BEGIN_OBJECT:
	                jsonReader.beginObject();
	                break;
	            case END_OBJECT:
	                jsonReader.endObject();
	                break;
	            case NAME:
	                jsonReader.nextName();
	                break;
	            case STRING:
	            case NUMBER:
	            case BOOLEAN:
	            case NULL:
	                jsonReader.skipValue();
	                break;
	            case END_DOCUMENT:
	                break loop;
	            default:
	                throw new AssertionError(token);
	            }
	        }
	        return true;
	    } catch ( final MalformedJsonException ignored ) {
	        return false;
	    } catch (IOException | AssertionError e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void showAboutDialog(String text, String title, String url){
		int result = JOptionPane.showOptionDialog(CellPackerMain.getMainFrame(), text, title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Go to website", "Close"}, 0);
		if(result == 1) return;
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static void addJSON(JsonObject base, JsonObject add){
		for(String s : add.keySet()){
			JsonElement element = add.get(s);
			//If base does not have this tag, just add it
			if(!base.has(s)){
				base.add(s, element);
			}else{ //If it is already there, replace or merge
				JsonElement there = base.get(s);
				if(there instanceof JsonObject && element instanceof JsonObject){ //If both are objects, merge them
					addJSON((JsonObject) there, (JsonObject) element);
				}else if(there instanceof JsonArray && element instanceof JsonArray){ //if they are arrays, append the entries of add to base
					//Iterate over new Array entries
					for(JsonElement addEntry : (JsonArray) element){
						if(addEntry instanceof JsonObject){ //If the element to append is an object
							JsonObject toMerge = (JsonObject) addEntry;
							if(toMerge.has("CPINDEX")){ //Can replace
								int cp = toMerge.get("CPINDEX").getAsInt();
								toMerge.remove("CPINDEX");
								JsonElement cdbEntry = ((JsonArray) there).get(cp); 
								if(cdbEntry instanceof JsonObject){
									addJSON((JsonObject) cdbEntry, toMerge);
								}else{
									((JsonArray) there).add(addEntry);
								}
							}else{
								((JsonArray) there).add(addEntry);
							}
						}else{
							((JsonArray) there).add(addEntry);
						}
					}
				}else{ //if they are primitives or different types, replace
					base.add(s, element);
				}
			}
		}
	}
	
	public static void removeJSON(JsonObject base, JsonObject remove){
		for(String s : remove.keySet()){
			JsonElement element = remove.get(s);
			//If base does not have this tag, just ignore
			if(!base.has(s)){
				//Ignore
			}else{ //If it is already there, remove or recursive remove 
				JsonElement there = base.get(s);
				if(there instanceof JsonObject && element instanceof JsonObject){ //If both are objects, recurse
					removeJSON((JsonObject) there, (JsonObject) element);
				}else if(there instanceof JsonArray && element instanceof JsonArray){ //if they are arrays, get Entries and remove them
					List<JsonElement> entriesThere = new ArrayList<>();
					List<JsonElement> entriesRemove = new ArrayList<>();
					((JsonArray) there).forEach((e) -> entriesThere.add(e));
					((JsonArray) element).forEach((e) -> entriesRemove.add(e));
					entriesThere.removeIf((e) -> entriesRemove.contains(e));
					JsonArray newArray = new JsonArray(entriesThere.size());
					entriesThere.forEach((e) -> newArray.add(e));
					base.add(s, newArray);
				}else{ //if they are primitives or different types, remove
					base.remove(s);
				}
			}
		}
	}
	
	public static <T> boolean contains(Iterable<T> l, Predicate<T> test){
		for(T t : l){
			if(test.test(t))
				return true;
		}
		return false;
	}
	
	public static <T> T getFirst(Iterable<T> l, Predicate<T> test){
		for(T t : l){
			if(test.test(t))
				return t;
		}
		return null;
	}
}
