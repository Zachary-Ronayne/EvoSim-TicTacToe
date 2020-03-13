package neuralNet;

public class Net{
	
	/**
	 * The amount the mutability can change each mutation
	 */
	public static final double MUABILITY_CHANGE = .1;
	/**
	 * The max mutability can be
	 */
	public static final double MAX_MUTABILITY = 2;
	/**
	 * The min mutability can be
	 */
	public static final double MIN_MUTABILITY = -2;
	
	/**
	 * The nodes of this neural net, must always have at least 2 layers, input and output, the rest are hidden layers
	 */
	private Node[][] nodes;
	
	/**
	 * A number in the range [-2, 2] that represents the range that this Net can change curing a mutation
	 */
	private double mutability;
	
	/**
	 * @param layers the number of nodes in each layer, must be at least 2 layers, the first is the input, the last is the output, the rest are hidden
	 */
	public Net(int... layers){
		nodes = new Node[layers.length][];
		for(int i = 0; i < nodes.length; i++) nodes[i] = new Node[layers[i]];

		for(int i = 0; i < nodes.length; i++){
			for(int j = 0; j < nodes[i].length; j++){
				if(i == 0) nodes[i][j] = new Node(0);
				else nodes[i][j] = new Node(nodes[i - 1].length);
			}
		}
		
		mutability = MAX_MUTABILITY - Math.random() * Math.abs(MAX_MUTABILITY - MIN_MUTABILITY);
	}
	
	/**
	 * Give the neural net input data, inputs must be the same length as the first node layer of the net.
	 * @param inputs
	 */
	public void feedInputs(double[] inputs){
		for(int i = 0; i < inputs.length; i++) nodes[0][i].setValue(inputs[i]);
	}
	
	/**
	 * @return the array of the output nodes of this net
	 */
	public double[] getOutputs(){
		double[] d = new double[nodes[nodes.length - 1].length];
		for(int i = 0; i < d.length; i++) d[i] = nodes[nodes.length - 1][i].getValue();
		return d;
	}
	
	/**
	 * Calculates the values of the nodes in this net based on the current input data
	 */
	public void calculateValues(){
		for(int i = 1; i < nodes.length; i++){
			double[] input = new double[nodes[i - 1].length];
			for(int j = 0; j < nodes[i - 1].length; j++) input[j] = nodes[i - 1][j].getValue();
			for(int j = 0; j < nodes[i].length; j++) nodes[i][j].calculateValue(input);
		}
	}
	
	/**
	 * Get the nodes of this net
	 * @return
	 */
	public Node[][] getNodes(){
		return nodes;
	}
	
	/**
	 * Get a mutated version of this Net, based on the mutability of this net
	 * @return
	 */
	public Net getMutatedNet(){
		Net returnN = new Net(0);
		double useMut = getNewMutability();
		
		Node[][] n = new Node[nodes.length][0];
		for(int i = 0; i < n.length; i++){
			n[i] = new Node[nodes[i].length];
			for(int j = 0; j < n[i].length; j++){
				n[i][j] = nodes[i][j].getMutatedNode(useMut);
			}
		}
		returnN.nodes = n;
		returnN.mutability = useMut;
		return returnN;
	}
	
	/**
	 * @return a new mutability based on this objects mutability
	 */
	public double getNewMutability(){
		return Math.min(MAX_MUTABILITY, Math.max(MIN_MUTABILITY, mutability + (Math.random() - .5) * MUABILITY_CHANGE));
	}
	
	/**
	 * @return the mutability of this object
	 */
	public double getMutability(){
		return mutability;
	}
	
}
