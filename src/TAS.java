import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class TAS {

    private static Window window;
    private static JPanel startUpPanel;
    private static JPanel editor;

    private static Script script;
    private static File currentFile;

    private static boolean stickWindowIsOpen;

    private static final LookAndFeel original = UIManager.getLookAndFeel();



    public TAS (){
        startProgram();
    }

    public static void startProgram (){
        startUpPanel = new JPanel();

        window = new Window();
        window.setSize(300,200);
        window.add(startUpPanel);

        JButton createNewScriptButton = new JButton("create new script");
        JButton loadScriptButton = new JButton("load script");



        createNewScriptButton.addActionListener(e -> {
            onNewScriptButtonPress();
        });

        loadScriptButton.addActionListener(e -> {
            onLoadButtonPress();
        });


        startUpPanel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

        startUpPanel.add(createNewScriptButton);
        startUpPanel.add(loadScriptButton);

    }

    public static void onLoadButtonPress (){
        openLoadFileChooser();
    }

    public static void onNewScriptButtonPress (){
    	openNewFileCreator();
    }

    public static void openLoadFileChooser (){

        setWindowsLookAndFeel();

        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());

        fileChooser.setDialogTitle("Choose existing TAS file");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt", "text");
        fileChooser.setFileFilter(filter);

        int option = fileChooser.showOpenDialog(null);

        if (option == JFileChooser.APPROVE_OPTION) {

            File fileToOpen = fileChooser.getSelectedFile();
            currentFile = fileToOpen;

            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(fileToOpen))) {

                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    stringBuilder.append(sCurrentLine).append("\n");
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            String string = stringBuilder.toString();
            script = new Script(string);

            try {
                UIManager.setLookAndFeel(original);
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            startEditor();
        }

        try {
            UIManager.setLookAndFeel(original);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static void openNewFileCreator (){


        setWindowsLookAndFeel();

        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());

        fileChooser.setDialogTitle("Choose where you want your TAS file to go");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt", "text");
        fileChooser.setFileFilter(filter);
        fileChooser.setSelectedFile(new File("script1.txt"));

        int option = fileChooser.showSaveDialog(null);
        
        if (option == JFileChooser.APPROVE_OPTION) {

            File fileToOpen = fileChooser.getSelectedFile();
            String fileName = fileChooser.getSelectedFile().getPath();
            File file = new File(fileName);
            try {
				file.createNewFile();
				FileWriter fileWriter = new FileWriter(fileName);
				// optimize the below later
				fileWriter.write("1 NONE 0;0 0;0\n");
				fileWriter.write("2 NONE 0;0 0;0\n");
				fileWriter.write("3 NONE 0;0 0;0\n");
				fileWriter.write("4 NONE 0;0 0;0\n");
				fileWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            
            currentFile = fileToOpen;

            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(fileToOpen))) {

                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    stringBuilder.append(sCurrentLine).append("\n");
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            String string = stringBuilder.toString();
            script = new Script(string);

            try {
                UIManager.setLookAndFeel(original);
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            startEditor();
        }
        
    }

    public static void setWindowsLookAndFeel (){
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static void startEditor (){


        window.remove(startUpPanel);

        editor = new JPanel();
        window.add(editor);

        editor.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

        String[] columnNames = {
                "frame",
                "L-stick",
                "R-Stick",
                "A",
                "B",
                "X",
                "Y",
                "ZR",
                "ZL",
                "R",
                "L",
                "DP-R",
                "DP-L",
                "DP-U",
                "DP-D"
        };

        DefaultTableModel model = new DefaultTableModel();

        for (String colName: columnNames){
            model.addColumn(colName);
        }





        JTable pianoRoll= new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }


        };

        pianoRoll.setRowSelectionAllowed(false);

        pianoRoll.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_A){

                    script.getInputLines().add(new InputLine((script.getInputLines().size() + 1) + " NONE 0;0 0;0"));

                    InputLine currentLine = script.getInputLines().get(script.getInputLines().size() - 1);

                    Object[] tmp = new Object[columnNames.length - 3];
                    tmp[0] = script.getInputLines().size() ;
                    addRow(currentLine, tmp, columnNames, model);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        pianoRoll.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = pianoRoll.rowAtPoint(evt.getPoint());
                int col = pianoRoll.columnAtPoint(evt.getPoint());

                if (row >= 0 && col >= 3) {

                    if (pianoRoll.getModel().getValueAt(row,col).equals(" ")) {
                        pianoRoll.getModel().setValueAt(pianoRoll.getColumnName(col), row, col);

                        script.getInputLines().get(row).buttonsEncoded.add(pianoRoll.getColumnName(col));

                    }else if (pianoRoll.getModel().getValueAt(row,col).equals(pianoRoll.getColumnName(col))){

                        pianoRoll.getModel().setValueAt(" ",row,col);
                        script.getInputLines().get(row).buttonsEncoded.remove(pianoRoll.getColumnName(col));
                    }

                }else if (col <= 2 && col > 0){
                    JFrame stickWindow;
                    if (!stickWindowIsOpen) {
                        stickWindow = new JFrame();
                        
                        stickWindowIsOpen = true;
                        stickWindow.setResizable(false);
                        stickWindow.setVisible(true);
                        stickWindow.setSize(300,400);
                        stickWindow.setLocation(new Point(200,200));

                        stickWindow.addWindowListener(new WindowAdapter()
                        {
                            @Override
                            public void windowClosing(WindowEvent e)
                            {
                                stickWindowIsOpen = false;
                                e.getWindow().dispose();
                            }
                        });


                        StickImagePanel stickImagePanel = new StickImagePanel(new StickPosition(Integer.parseInt(script.inputLines.get(row).getStickL().split(";")[0]),Integer.parseInt(script.inputLines.get(row).getStickL().split(";")[1])));
                        stickWindow.add(stickImagePanel);
                        
                    }

                }
            }
        });

        for (int i = 0; i < script.getInputLines().size(); i++){
            InputLine currentLine = script.getInputLines().get(i);

            Object[] tmp = new Object[columnNames.length - 3];
            tmp[0] = i + 1;
            addRow(currentLine, tmp, columnNames, model);
        }



        JScrollPane scrollPane = new JScrollPane(pianoRoll);

        pianoRoll.getTableHeader().setResizingAllowed(false);
        pianoRoll.getTableHeader().setReorderingAllowed(false);

        window.setSize(700,1100);
        editor.setSize(550,550);
        pianoRoll.setSize(500,700);

        pianoRoll.getColumnModel().getColumn(0).setPreferredWidth(300);
        pianoRoll.getColumnModel().getColumn(1).setPreferredWidth(300);
        pianoRoll.getColumnModel().getColumn(2).setPreferredWidth(300);



        for (int i = 3; i < 11; i++){
           pianoRoll.getColumnModel().getColumn(i).setPreferredWidth(60);
        }

        for (int i = 11; i < 15; i++){
            pianoRoll.getColumnModel().getColumn(i).setPreferredWidth(200);
        }

        editor.add(scrollPane);

        JButton saveFileButton = new JButton("Save file");

        saveFileButton.addActionListener(e -> {
            saveFile();
        });

        editor.add(saveFileButton);
    }

    private static void saveFile (){


        BufferedWriter writer = null;
        try {

            StringBuilder wholeScript = new StringBuilder();

            for (InputLine currentLine: script.getInputLines()){
                wholeScript.append(currentLine.getFull() + "\n");
            }

            FileWriter fw = null;

            fw = new FileWriter(currentFile);


            writer = new BufferedWriter(fw);


            writer.write(wholeScript.toString());

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        finally
        {
            try{
                if (writer != null)
                    writer.close();
            }catch (Exception ex){
                System.out.println("Error in closing the BufferedWriter" + ex);
            }
        }

    }

    private static void addRow(InputLine currentLine, Object[] tmp, String[] columnNames, DefaultTableModel model) {
        tmp[1] = currentLine.getStickL();
        tmp[2] = currentLine.getStickR();

        for (int j = 3; j < tmp.length; j++){
            if (currentLine.getButtonsEncoded().contains(columnNames[j])){
                tmp[j] = columnNames[j];
            }else{
                tmp[j] = " ";
            }
        }
        model.addRow(tmp);
    }


    public static void main(String[] args) {
        new TAS();
    }
}
