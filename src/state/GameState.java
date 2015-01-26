package state;
/**
 * Contain the state of the game for a particular player, 
 * which is constitute with the state of the model and the state of the object playerChoice
 * @author robin
 *
 */
public class GameState {

	private ModelState modelState;
	private PlayerState playerState;//not use already
	
	public GameState(){
	}
	
	public void setModelState(ModelState state){
		this.modelState=state;
	}
	
	public ModelState getModelState(){
		return modelState;
	}
	
	/**
	 * This methode allow to set the state of model whit an int instead of an ModelState enum (currently from 0 to 2)
	 * @param state 0 correspond to the phase westeros,1 for the programation, 2 for execution
	 */
	public void setModelState(int state){
		switch (state){
		case 0 :
			this.modelState=ModelState.PHASE_WESTEROS;
			break;
		case 1 :
			this.modelState=ModelState.PHASE_PROGRAMATION;
			break;
		case 2 :
			this.modelState=ModelState.PHASE_EXECUTION;
			break;
		}
	}
}
