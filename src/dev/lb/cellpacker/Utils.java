package dev.lb.cellpacker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.io.ByteArrayOutputStream;
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
		//Iterate over all entries in add
		for(String addKey : add.keySet()){
			//get the current entry as an object
			JsonElement addElement = add.get(addKey); 
			//If the base tag does not have this tag yet, add this tag to base
			if(!base.has(addKey)){
				base.add(addKey, addElement);
			}else{ //If it does exist, try to merge depending on the type
				addJsonByType(base.get(addKey), addElement, (e) -> base.add(addKey, e));
			}
		}
	}
	
	private static void addJsonByType(JsonElement baseElement, JsonElement addElement, Consumer<JsonElement> overwriteAction){
		//The simplest is a primitive, it is just replaced:
		if(baseElement.isJsonPrimitive() && addElement.isJsonPrimitive()){
			overwriteAction.accept(addElement);
		//If they are both objects, recursively call this merge method
		}else if(baseElement.isJsonObject() && addElement.isJsonObject()){
			addJSON(baseElement.getAsJsonObject(), addElement.getAsJsonObject());
		//If they are arrays, find markers or append
		}else if(baseElement.isJsonArray() && addElement.isJsonArray()){
			//save the elements in new variables
			JsonArray addArray = addElement.getAsJsonArray();
			JsonArray baseArray = baseElement.getAsJsonArray();
			//Then iterate over the added array
			for(JsonElement addArrayElement : addArray){
				//Handle depending on type
				//If it is an object, look for markers
				if(addArrayElement.isJsonObject()){
					//Check for CPWHERE (overrides CPINDEX) and if it is an object
					if(addArrayElement.getAsJsonObject().has("CPWHERE") &&
							addArrayElement.getAsJsonObject().get("CPWHERE").isJsonObject()){
						JsonObject whereObject = addArrayElement.getAsJsonObject().get("CPWHERE").getAsJsonObject();
						//Test if where has the Strings 'id' and 'value'
						if(whereObject.has("id") && whereObject.get("id").isJsonPrimitive() &&
								whereObject.has("value") && whereObject.get("value").isJsonPrimitive()){
							String id = whereObject.get("id").getAsString();
							String value = whereObject.get("value").getAsString();
							//Now find an entry in the base array that has id==value
							for(int i = 0; i < baseArray.size(); i++){
								JsonElement baseArrayElement = baseArray.get(i);
								//The element has to be an object and has to have a tag called id that is a primitive
								if(baseArrayElement.isJsonObject() && baseArrayElement.getAsJsonObject().has(id) &&
										baseArrayElement.getAsJsonObject().get(id).isJsonPrimitive()){
									//Value has to be equal
									if(baseArrayElement.getAsJsonObject().get(id).getAsString().equals(value)){
										//We found a tag
										final int index = i; //This is necessary, lambda doesn't like non-finals 
										addJsonByType(baseArrayElement, addArrayElement, (e) -> baseArray.set(index, e));
										//Then break out of the for loop, only the first item should be modified
										break;
									}
								}
							}
						}else{
							System.err.println("Json Parser: Found CPWHERE tag, but 'id' or 'value' tags were missing");
						}
						//if the reqired fields do not exist, do nothing
						
					//Check for CPINDEX next and if it is a primitive
					}else if(addArrayElement.getAsJsonObject().has("CPINDEX") &&
							addArrayElement.getAsJsonObject().get("CPINDEX").isJsonPrimitive()){
						//Check if the index is within range
						int index = addArrayElement.getAsJsonObject().get("CPINDEX").getAsInt();
						if(index >= 0 && index < baseArray.size()){
							//Get the element from the base array
							JsonElement baseArrayElement = baseArray.get(index);
							addJsonByType(baseArrayElement, addArrayElement, (e) -> baseArray.set(index, e));
						}else{
							System.err.println("Json Parser: Found CPINDEX tag, but index was out of bounds");
						}
						//If the index is out of bounds, do nothing
					//If no marker is found, simply append
					}else{
						baseArray.add(addArrayElement);
					}
				//Primitives and arrays (can you even put arrays in arrays in json?) cannot have a marker and are appended
				}else{
					baseArray.add(addArrayElement);
				}
			}
		//If they are different types, they are also replaced
		}else{
			overwriteAction.accept(addElement);
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
	
	public static byte[] concat(byte[] a, byte[] b){
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
	    System.arraycopy(b, 0, c, a.length, b.length);
	    return c;
	}
	
	public static byte[] encodeInt(int num){
		return new byte[] {
	            (byte)(num),
	            (byte)(num >>> 8),
	            (byte)(num >>> 16),
	            (byte)(num >>> 24)};
	}
	
	public static int decodeInt(byte[] num){
		if(num.length != 4)
			throw new NumberFormatException("Array size must be 4");
		return ((num[0]) & 0xFF) + ((num[1] & 0xFF) << 8) +
				((num[2] & 0xFF) << 16) + ((num[3] & 0xFF) << 24);
	}
	
	public static void writeInt(ByteArrayOutputStream out, int i) throws IOException{
		out.write(encodeInt(i));
	}
	
	public static void writeString(ByteArrayOutputStream out, String s, byte terminator) throws IOException{
		out.write((byte) s.length());
		out.write(s.getBytes());
		out.write(terminator);
	}
	
}
