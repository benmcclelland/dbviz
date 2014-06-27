package gui.controllers;

import java.util.List;

import gui.models.Line;
import gui.models.Replacements;

/**
 * Controller between model and graph view
 */
public class GraphController extends AbstractController {
	
	// common attributes between models and views
	public static final String LINES = "Lines";
	public static final String LEGEND_REPLACEMENTS	= "LegendReplacements";
	public static final String LABEL_REPLACEMENTS = "LabelReplacements";
	
	/**
	 * Change lines in the model
	 * @param newLines new lines
	 */
	public void changeLines(List<Line> newLines) {
		setModelProperty(LINES, newLines);
	}
	
	/**
	 * Change replacements in the model
	 * @param newReplacements new replacements
	 */
	public void changeReplacements(Replacements newReplacements) {
		setModelProperty(LEGEND_REPLACEMENTS, newReplacements);
	}

}
