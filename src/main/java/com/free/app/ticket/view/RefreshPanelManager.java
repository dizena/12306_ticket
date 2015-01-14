package com.free.app.ticket.view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.free.app.ticket.TicketMainFrame;
import com.free.app.ticket.model.PassengerData;
import com.free.app.ticket.model.TicketBuyInfo;
import com.free.app.ticket.model.TicketConfigInfo;
import com.free.app.ticket.model.TrainConfigInfo;
import com.free.app.ticket.service.AutoBuyThreadService;

/**
 * 
 * <刷票面板UI管理类>
 */
public class RefreshPanelManager {

	private static JPanel controller_panel = null;

	private static boolean isInited = false;

	private static JButton startButton;

	private static JButton endButton;

	/**
	 * <界面初始化调用>
	 * 
	 */
	public static void init(TicketMainFrame frame) {
		if (!isInited) {
			isInited = true;
			controller_panel = new JPanel();
			controller_panel.setLayout(new FlowLayout());
			controller_panel.setBounds(10, 390, 780, 50);
			frame.getContentPane().add(controller_panel);

			startButton = new StartBtn("开始刷票");
			controller_panel.add(startButton);

			endButton = new JButton("停止刷票");
			controller_panel.add(endButton);
			endButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (JOptionPane.showConfirmDialog(TicketMainFrame.frame,
							"确认停止刷票吗？", "请选择", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					    stop();
					}
				}
			});
		}
	}
	
	public static void stop(){
	    TicketMainFrame.isStop = true;
        startButton.setEnabled(true);
	}

	static class StartBtn extends JButton {

		/**
		 * 注释内容
		 */
		private static final long serialVersionUID = 1L;

		public StartBtn(String text) {
			super(text);

			this.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					if (!LoginPanelManager.isLogged()) {
						TicketMainFrame.remind("请先登录!");
						return;
					}

					List<PassengerData> passengers = PassengerPanelManager
							.getPassenger();
					if (passengers.isEmpty()) {
						TicketMainFrame.remind("请至少填写一个购票乘客的信息!");
						return;
					}

					TicketConfigInfo ticketConfigInfo = ConfigPanelManager
							.getTicketConfigInfo();
					if (ticketConfigInfo == null || ticketConfigInfo.getTrainDateAlias() == null)
						return;
					
					TrainConfigInfo trainConfigInfo = ConfigPanelManager.getTrainConfigInfo();

					StartBtn.this.setEnabled(false);

					String tipsMsg = "===开始为乘客[";
					for (PassengerData passenger : passengers) {
						tipsMsg += passenger.getName() + ",";
					}
					tipsMsg = tipsMsg.substring(0, tipsMsg.length() - 1);
					tipsMsg += "]刷票，请耐心等待===";
					TicketMainFrame.trace(tipsMsg);
					
					TicketMainFrame.isStop = false;
					new AutoBuyThreadService(new TicketBuyInfo(ticketConfigInfo,
							passengers, trainConfigInfo)).start();
				}

			});
		}

	}

}
