package com.nmims.helpers;

import com.nmims.beans.BaseStudentPortalBean;
import com.nmims.beans.WalletBean;

public class WalletHelper{
	private void updateWalletBalance(WalletBean bean,String transactionType) {
		double newBalance = "CREDIT".equals(transactionType) ? Double.parseDouble(bean.getBalance()) + Double.parseDouble(bean.getAmount()) : Double.parseDouble(bean.getBalance()) - Double.parseDouble(bean.getAmount());
		bean.setWalletBalance(newBalance+"");
		bean.setBalance(newBalance+"");
	}
}
