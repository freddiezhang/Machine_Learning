import java.util.HashMap;


public class Instance {
	String[] inputs;
	String label;
	Instance(String[] inputs, String label){
		this.inputs = inputs;
		this.label = label;
	}
	public String[] getInputs(){
		return inputs;
	}
	public String getLabel(){
		return label;
	}
}
