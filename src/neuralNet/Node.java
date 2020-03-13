package neuralNet;

public class Node{
	
	/**
	 * The value of this node before any weights are considered. Always in range [-1, 1]
	 */
	private double bias;
	
	/**
	 * All of the weights going into this node, each weight is in range [-1, 1]
	 */
	private double[] weights;
	
	/**
	 * The current value of this node based on the last data fed into this node
	 */
	private double value;
	
	/**
	 * Create a node with numWeights going into it, the bias and every weight is randomized
	 * @param numWeights
	 */
	public Node(int numWeights){
		weights = new double[numWeights];
		for(int i = 0; i < weights.length; i++) weights[i] = getRandom();
		bias = getRandom();
		value = bias;
	}
	
	/**
	 * Get the weight at the given index
	 * @param i
	 * @return
	 */
	public double getWeight(int i){
		return weights[i];
	}
	
	/**
	 * @param input each value that will be multiplied by each weight, must be the same size as weights
	 */
	public void calculateValue(double[] input){
		double total = bias;
		for(int i = 0; i < weights.length; i++) total += weights[i] * input[i];
		value = sigmoid(total);
	}
	
	/**
	 * Set the value of this node to the given v, should only be used for input nodes
	 * @param v
	 */
	public void setValue(double v){
		value = v;
	}
	
	/**
	 * @return the current value of this node, based on the last input
	 */
	public double getValue(){
		return value;
	}
	
	/**
	 * Make a copy of this node with a modified bias and weights based on the given mutability
	 * @param mutability
	 * @return
	 */
	public Node getMutatedNode(double mutability){
		Node n = new Node(weights.length);
		for(int i = 0; i < weights.length; i++){
			n.weights[i] = validRange(weights[i] + (Math.random() - .5) * mutability);
		}
		n.bias = validRange(bias + (Math.random() - .5) * mutability);
		return n;
	}
	
	/**
	 * @param d
	 * @return d, but if d is less than -1 returns -1, and if d is greater than 1, returns 1
	 */
	public static double validRange(double d){
		return Math.max(-1, Math.min(1, d));
	}
	
	/**
	 * @return a random number in the range of [-1, 1]. Valid for weights and biases
	 */
	public static double getRandom(){
		return (Math.random() - .5) * 2;
	}
	
	/**
	 * Returns the sigmoid function of x, except adjusted to be in the range of [-1, 1]
	 * @param x
	 * @return
	 */
	public static double sigmoid(double x){
		return 2.0 / (1.0 + Math.pow(Math.E, -x)) - 1.0;
	}
}
