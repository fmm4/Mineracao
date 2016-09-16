package front;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;

import back.Searcher;

public class Menu {

	protected Shell shell;
	private Text text;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Menu window = new Menu();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(484, 175);
		shell.setText("Minerador");
		shell.setLayout(null);
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(10, 10, 446, 26);
		
		
		Button btnSearch = new Button(shell, SWT.NONE);
		btnSearch.setBounds(10, 94, 90, 30);
		btnSearch.setText("Procurar");
		

		
		Button btnNoStopwords = new Button(shell, SWT.CHECK);
		btnNoStopwords.setBounds(10, 42, 124, 20);
		btnNoStopwords.setText("Sem Stopwords");
		
		Button btnStemming = new Button(shell, SWT.CHECK);
		btnStemming.setBounds(10, 68, 111, 20);
		btnStemming.setText("Stemming");
		
		btnSearch.addSelectionListener(new SelectionListener(){
			
			public void widgetSelected(SelectionEvent event){
				String queryString = text.getText();
				try {
					Searcher.search(queryString, btnNoStopwords.getSelection(), btnStemming.getSelection());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});


	}
	
	
}
