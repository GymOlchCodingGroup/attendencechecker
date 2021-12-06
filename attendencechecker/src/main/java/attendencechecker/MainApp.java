package attendencechecker;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import javax.swing.JRootPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import attendencechecker.grabber.QRCodeGrabber;
import attendencechecker.grabber.ScannerGrabber;
import attendencechecker.observer.Observer;

public class MainApp implements Observer<String> {

	private Display display;
	protected Shell shell;
	private Text txtFirstname;
	private Text txtSecondname;
	private Text txtClass;
	private Text txtEmail;

	private Webcam webcam;
	private WebcamPanel panel;
	private List<Webcam> webcams;
	private Text txtResult;
	private Label lblAlert;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			MainApp window = new MainApp();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		QRCodeGrabber qrCodeGrabber = new QRCodeGrabber(webcam);
		qrCodeGrabber.register(this);
		qrCodeGrabber.start();
		ScannerGrabber scannerGrabber = new ScannerGrabber();
		scannerGrabber.register(this);
		scannerGrabber.start();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		panel.stop();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(600, 500);
		shell.setText("Attendence-Check");
		shell.setLayout(new GridLayout(2, false));
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				panel.stop();
			}
		});

		final Dimension size = WebcamResolution.QVGA.getSize();
		webcam = Webcam.getDefault();
		webcam.setViewSize(size);

		lblAlert = new Label(shell, SWT.NONE);
		lblAlert.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		lblAlert.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAlert.setText("");

		Composite compositeCamera = new Composite(shell, SWT.EMBEDDED);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite.widthHint = size.width;
		gd_composite.heightHint = size.height;
		compositeCamera.setLayoutData(gd_composite);
		Frame frame = SWT_AWT.new_Frame(compositeCamera);
		final JRootPane root = new JRootPane();

		frame.add(root);

		panel = new WebcamPanel(webcam, size, true);
		panel.setMirrored(true);
		System.out.println("Panel is valid: " + panel.isValid());
		root.getContentPane().add(panel);
		System.out.println("Panel is valid: " + panel.isValid());
		new Label(shell, SWT.NONE);

		final Combo combo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		webcams = Webcam.getWebcams();
		String[] items = new String[webcams.size()];
		int i = 0;
		for (Webcam webcam : webcams) {
			items[i] = webcam.getName();
			i++;
		}
		combo.setItems(items);
		combo.select(0);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				panel.stop();
				root.getContentPane().remove(panel);

				int idx = combo.getSelectionIndex();
				String cameraName = combo.getItem(idx);
				Webcam webcam = Webcam.getWebcamByName(cameraName);

				if (webcam != null) {
					System.out.println("New Camera picked");
					System.out.println("new Webcam picked:" + webcam.getName());
					webcam.close();
					webcam.setViewSize(size);
					panel = new WebcamPanel(webcam, false);
					System.out.println(panel.isErrored());
					root.getContentPane().add(panel);
					System.out.println("Panel is valid: " + panel.isValid());
					panel.start();
					shell.pack();
					shell.layout();
				} else {
					System.out.println("No webcams found...");
				}
			}
		});

		Label lblFirstname = new Label(shell, SWT.NONE);
		lblFirstname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFirstname.setText("Vorname");

		txtFirstname = new Text(shell, SWT.BORDER);
		txtFirstname.setEditable(false);
		txtFirstname.setText("FirstName");
		txtFirstname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblSecondname = new Label(shell, SWT.NONE);
		lblSecondname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSecondname.setText("Nachname");

		txtSecondname = new Text(shell, SWT.BORDER);
		txtSecondname.setEditable(false);
		txtSecondname.setText("SecondName");
		txtSecondname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblClass = new Label(shell, SWT.NONE);
		lblClass.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblClass.setText("Klasse");

		txtClass = new Text(shell, SWT.BORDER);
		txtClass.setEditable(false);
		txtClass.setText("Class");
		txtClass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblEmail = new Label(shell, SWT.NONE);
		lblEmail.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEmail.setText("E-Mail");

		txtEmail = new Text(shell, SWT.BORDER);
		txtEmail.setEditable(false);
		txtEmail.setText("E-Mail");
		txtEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblResult = new Label(shell, SWT.NONE);
		lblResult.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblResult.setText("Result");

		txtResult = new Text(shell, SWT.BORDER);
		txtResult.setEditable(false);
		txtResult.setText("Result");
		txtResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	}

	public void update(final String text) {
		display.asyncExec(new Runnable() {

			public void run() {
				if (!txtResult.isDisposed()) {
					txtResult.setText(text);
					txtResult.getParent().layout();
				}
				if (!lblAlert.isDisposed()) {
					lblAlert.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
					lblAlert.getParent().layout();
				}
			}
		});
	}
}
