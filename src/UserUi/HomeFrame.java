package UserUi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JProgressBar;
import javax.swing.JList;
import javax.swing.JScrollPane;

import Action.Classfy;
import Action.WordCut;
import Helper.FileHelper;

import java.awt.GridLayout;
import javax.swing.ListSelectionModel;

public class HomeFrame extends JFrame {

	private JPanel contentPane;
	private JButton chooseBtn;
	private JButton startBtn;
	private JTextField textField;
	private JList classList;
	private DefaultListModel<String> classListModel;
	private JScrollPane scrollPane;
	private JPanel viewPanel;
	public JProgressBar progressBar;
	
	/**
	 * 用户选择的文件数组
	 */
	private File[] userFiles = null;
	
	/**
	 * 分类结果列表
	 */
	private HashMap<String, ArrayList<File>> resultMap = new HashMap<String,ArrayList<File>>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomeFrame frame = new HomeFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public HomeFrame() {
		initComponents();
		initData();
		bindChooseBtnEvent();
		bindStartBtnEvent();
		bindClassListEvent();
	}
	
	private void initData(){
//		new Thread(){
//			public void run(){
//				try {
//					int index = 0;
//					while(index<100){
//						index = progressBar.getValue();
//						index ++;
//						progressBar.setValue(index);
//						Thread.sleep(50);
//					}
//				} catch (Exception e) {
//				}
//				
//			}
//		}.start();
	}
	
	public void updateProgressBar(int curIndex){
		if(curIndex <= userFiles.length){
			progressBar.setValue(curIndex);
		}
	}
	
	/**
	 * 更新左侧分类查看器列表
	 */
	private void updateClassList(){
		classListModel = new DefaultListModel<String>();
		Iterator<String> iterator = resultMap.keySet().iterator();
		while (iterator.hasNext()) {
			String className = (String) iterator.next();
			classListModel.addElement(className);
		}
		classList.setModel(classListModel);
	}
	
	/**
	 * 绑定选择按钮的事件
	 */
	private void bindChooseBtnEvent(){
		chooseBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("txt&doc","txt","doc");
				chooser.setFileFilter(filter);
				chooser.setCurrentDirectory(new File("E:\\android\\windows\\Classification\\article"));
				chooser.setMultiSelectionEnabled(true);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = chooser.showOpenDialog(null);
				if(result == JFileChooser.APPROVE_OPTION){
					System.out.println(chooser.getSelectedFile().getAbsolutePath());
					userFiles = chooser.getSelectedFiles();
					updateViewPanel(userFiles);
					progressBar.setMaximum(userFiles.length);
					progressBar.setMinimum(0);
					progressBar.setValue(0);
					//允许开始分类按钮
					startBtn.setEnabled(true);
				}
			}
		});
	}
	
	/**
	 * 开始分类按钮事件
	 */
	private void bindStartBtnEvent(){
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(){
					public void run(){
						try {
							//进行分词
							WordCut.run(userFiles, HomeFrame.this);
							//进行分类
							resultMap = Classfy.run(userFiles);
							//更新左侧分类查看器
							updateClassList();
							//分类完成时移除右侧面板显示的文件
							updateViewPanel(new File[]{});
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}.start();
			}
		});
	}
	
	private void bindClassListEvent(){
		classList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String className = (String)classList.getSelectedValue();
				updateViewPanel(resultMap.get(className).toArray(new File[]{}));
			}
		});
	}
	
	/**
	 * 更新文件显示区域的ui
	 * @param files
	 */
	private void updateViewPanel(File[] files){
		int height = ((files.length / 5) + 1) * 140;
		viewPanel = new JPanel();
		scrollPane.setViewportView(viewPanel);
		viewPanel.setPreferredSize(new Dimension(400, height));
		viewPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		viewPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		for (File file : files) {
			JButton button = new JButton();
			button.setText(file.getName());
			button.setPreferredSize(new Dimension(82, 120));
			if(FileHelper.getFileExt(file).equals("doc")){
				button.setIcon(new ImageIcon("image/doc.jpg"));
			}else{
				button.setIcon(new ImageIcon("image/txt.jpg"));
			}
			button.setVerticalTextPosition(JButton.BOTTOM);
			button.setHorizontalTextPosition(JButton.CENTER);
			viewPanel.add(button);
			System.out.println(button.getText());
		}
		repaint();
	}
	
	/**
	 * 初始化控件布局
	 */
	private void initComponents(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		chooseBtn = new JButton("\u9009\u62E9\u6587\u4EF6");
		textField = new JTextField();
		textField.setColumns(10);
		
		startBtn = new JButton("\u5F00\u59CB\u5206\u7C7B");
		startBtn.setEnabled(false);
		
		JButton exportBtn = new JButton("\u5BFC\u51FA\u5206\u7C7B");
		exportBtn.setEnabled(false);
		
		progressBar = new JProgressBar();
		
		classList = new JList();
		classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		scrollPane = new JScrollPane();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 664, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, 388, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(chooseBtn)
							.addGap(6)
							.addComponent(startBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
							.addGap(3)
							.addComponent(exportBtn, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(classList, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
							.addGap(27)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 468, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(10, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(54)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addComponent(chooseBtn, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addComponent(startBtn, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addComponent(exportBtn, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE)
						.addComponent(classList, GroupLayout.PREFERRED_SIZE, 313, GroupLayout.PREFERRED_SIZE)))
		);
		
		viewPanel = new JPanel();
		scrollPane.setViewportView(viewPanel);
		contentPane.setLayout(gl_contentPane);
	
	}
}
