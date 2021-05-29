package io.github.jadefalke2;

import io.github.jadefalke2.util.CorruptedScriptException;

import javax.swing.table.DefaultTableModel;

public class Function extends Script{

	public Function(String script) throws CorruptedScriptException {
		super(script);
	}

	public void callFunction (DefaultTableModel table, Script mainScript, int row){
		for (int i = row; i < row + getInputLines().size(); i++){
			InputLine currentLineToInsert = getInputLines().get(i - row);

			if (i > table.getRowCount()){
				mainScript.getInputLines().add(currentLineToInsert);
				table.addRow(currentLineToInsert.getArray(i));
			} else {
				for (int j = 0; j < table.getColumnCount(); j++){
					switch (j){

						case 1:
							mainScript.getInputLines().get(i).setStickL(getInputLines().get(i - row).getStickL());
							table.setValueAt("",i,j);
							break;

						case 2:
							// Stick R
							break;

						// buttons
					}
				}
			}
		}
	}

}
