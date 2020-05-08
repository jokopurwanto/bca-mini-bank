package com.bca.minibank.mail;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;

public class ContentEmailWestBankPKWT {
	
	private String contentSubject;
	private String contentType;
	private String contentHeader;
	private String contentBody;
	private String contentFooter;
	
	public String getContentSubject() {
		return contentSubject;
	}
	public String getContentType() {
		return contentType;
	}
	
	public String getContentFull() {
		return contentHeader + contentBody + contentFooter;
	}
	
	public String formatRp(int nominal) {
		DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
		DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

		formatRp.setCurrencySymbol("Rp. ");
		formatRp.setMonetaryDecimalSeparator(',');
		formatRp.setGroupingSeparator('.');

		kursIndonesia.setDecimalFormatSymbols(formatRp);
		return kursIndonesia.format(nominal);
	}
	
	public void getContentPengajuanSetorTunai(int idTransaksi, Date tglPengajuan, String jnsTransaksi, String noRek, String noRekTujuan,int nominal) {
		contentSubject = "Pengajuan Setor Tunai";
		contentType = "text/html";
		contentHeader = "<h1>Website West Bank</h1>\r\n" + 
				"\r\n";
		contentBody = "<p> Melalui email ini kami informasikan bahwa Transaksi dengan detail sebagai berikut : </p>\r\n" + 
				"<table>\r\n" + 
				"	<tr>\r\n" + 
				"	  <td>No. Transaksi</td>\r\n" + 
				"	  <td> : </td>\r\n" + 
				"	  <td> "+ idTransaksi +" </td>\r\n" + 
				"	</tr>\r\n" + 
				"	<tr>\r\n" + 
				"	  <td>Tanggal, waktu Pengajuan Transaksi</td>\r\n" + 
				"	  <td> : </td>\r\n" + 
				"	  <td> "+ tglPengajuan +" </td>\r\n" + 
				"	</tr>\r\n" + 
				"	<tr>\r\n" + 
				"	  <td>Jenis Transaksi</td>\r\n" + 
				"	  <td> : </td>\r\n" + 
				"	  <td> "+ jnsTransaksi +" </td>\r\n" + 
				"	</tr>\r\n" + 
				"	<tr>\r\n" + 
				"	  <td>No. Rekening Asal</td>\r\n" + 
				"	  <td> : </td>\r\n" + 
				"	  <td> " + noRek+ "</td>\r\n" + 
				"	</tr>\r\n" + 
				"	<tr>\r\n" + 
				"	  <td>No. Rekening Tujuan</td>\r\n" + 
				"	  <td> : </td>\r\n" + 
				"	  <td> " + noRekTujuan + " </td>\r\n" + 
				"	</tr>\r\n" + 
				"	<tr>\r\n" + 
				"	  <td>Nominal</td>\r\n" + 
				"	  <td> : </td>\r\n" + 
				"	  <td> "+ formatRp(nominal) +" </td>\r\n" + 
				"	</tr>\r\n" + 
				"</table>\r\n" + 
				"<br>\r\n" + 
				" Telah <strong>Melakukan Pengajuan Setor Tunai</strong><br>\r\n" + 
				"Pengajuan akan segera di proses, mohon tunggu Approve dari Admin<br>"+
				"Anda dapat mengelakukan pengecekan saldo dengan login melalui website kami dengan menekan tombol dibawah ini :<br><br>\r\n" + 
				"<a href=\"http://localhost:8081/login\"><button style=\"  background-color: DarkBlue;border: none; color: white;padding: 15px 32px;\r\n" + 
				"  text-align: center;\r\n" + 
				"  text-decoration: none;\r\n" + 
				"  display: inline-block;\r\n" + 
				"  font-size: 16px;\r\n" + 
				"  margin: 4px 2px;\r\n" + 
				"  cursor: pointer;\">Website West Bank</button></a>"; 
		contentFooter =	"<br>\r\n" + 
				"<br>\r\n" + 
				"<p>Best Regards,</p>\r\n" + 
				"<p style=\"color:Blue;font-family:'Lucida Sans Unicode';font-size:18px\"> "+ "Admin" +" </p>\r\n" + 
				"<p> Admin West Bank </p>\r\n" + 
				"\r\n" + 
				"<h2>WEST BANK</h2>\r\n" + 
				"<p>Jl. Slipi Jaya No. 1 Jakarta Barat</p>\r\n" + 
				"<p>   Telp : (021) 08123456789 </p>";
	}

}
