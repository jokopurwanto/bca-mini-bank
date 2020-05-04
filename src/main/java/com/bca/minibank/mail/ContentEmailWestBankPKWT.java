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
	
//	public String getContentHeader() {
//		return contentHeader;
//	}
//
//	public String getContentBody() {
//		return contentBody;
//	}
//
//	public String getContentFooter() {
//		return contentFooter;
//	}

	public String getContentFull() {
		return contentHeader + contentBody + contentFooter;
	}


	public void getContentVerifyNewUser(String username, String adminName) {
		contentSubject = "Account User Anda Telah Terverifikasi";
		contentType = "text/html";
		contentHeader = "<h1>Selamat bergabung di Website West Bank! </h1>\r\n" + 
				"\r\n";
		contentBody = "<p> Account yang telah anda buat telah terverifikasi, dengan username : <Strong>"+
				username +
				"</Strong> dan password sesuai yang telah anda isi saat registrasi awal,</p>\r\n" + 
				"<p>silahkan melanjutkan registrasi dengan login kembali dengan username tersebut melalui website kami dengan menekan tombol berikut :\r\n" + 
				" </p>\r\n" + 
				"<a href=\"http://localhost:8081/login\"><button style=\"  background-color: DarkBlue;border: none; color: white;padding: 15px 32px;\r\n" + 
				"  text-align: center;\r\n" + 
				"  text-decoration: none;\r\n" + 
				"  display: inline-block;\r\n" + 
				"  font-size: 16px;\r\n" + 
				"  margin: 4px 2px;\r\n" + 
				"  cursor: pointer;\">Lanjutkan Registrasi</button></a>\r\n"; 
		contentFooter =	"<br>\r\n" + 
				"<br>\r\n" + 
				"<p>Best Regards,</p>\r\n" + 
				"<p style=\"color:Blue;font-family:'Lucida Sans Unicode';font-size:18px\"> "+ adminName +" </p>\r\n" + 
				"<p> Admin West Bank </p>\r\n" + 
				"\r\n" + 
				"<h2>WEST BANK</h2>\r\n" + 
				"<p>Jl. Slipi Jaya No. 1 Jakarta Barat</p>\r\n" + 
				"<p>   Telp : (021) 08123456789 </p>";
	}
	
	public void getContentNotVerifyNewUser(String username, String adminName, String keterangan) {
		contentSubject = "Account User Anda Ditolak";
		contentType = "text/html";
		contentHeader = "<h1>Website West Bank</h1>\r\n" + 
				"\r\n";
		contentBody = "<p> Account yang telah anda buat dengan username : <Strong>"+
				username +
				"</Strong> gagal terverifikasi, dikarenakan : "+ keterangan +"</p>\r\n" + 
				"<p>Silahkan memperbaiki data tersebut melalui website kami dengan menekan tombol berikut dan login dengan username dan password sesuai data registrasi awal:\r\n" + 
				" </p>\r\n" + 
				"<a href=\"http://localhost:8081/login\"><button style=\"  background-color: DarkBlue;border: none; color: white;padding: 15px 32px;\r\n" + 
				"  text-align: center;\r\n" + 
				"  text-decoration: none;\r\n" + 
				"  display: inline-block;\r\n" + 
				"  font-size: 16px;\r\n" + 
				"  margin: 4px 2px;\r\n" + 
				"  cursor: pointer;\">Perbaikan Registrasi</button></a>\r\n"; 
		contentFooter =	"<br>\r\n" + 
				"<br>\r\n" + 
				"<p>Best Regards,</p>\r\n" + 
				"<p style=\"color:Blue;font-family:'Lucida Sans Unicode';font-size:18px\"> "+ adminName +" </p>\r\n" + 
				"<p> Admin West Bank </p>\r\n" + 
				"\r\n" + 
				"<h2>WEST BANK</h2>\r\n" + 
				"<p>Jl. Slipi Jaya No. 1 Jakarta Barat</p>\r\n" + 
				"<p>   Telp : (021) 08123456789 </p>";
	}
	
	public String formatRp(Double nominal) {
		DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
		DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

		formatRp.setCurrencySymbol("Rp. ");
		formatRp.setMonetaryDecimalSeparator(',');
		formatRp.setGroupingSeparator('.');

		kursIndonesia.setDecimalFormatSymbols(formatRp);
		return kursIndonesia.format(nominal);
	}

	public void getContentSuccessSetorTunai(int idTransaksi, Date tglTransaksi, String jnsTransaksi, String noRekAsal, String noRekTujuan, Double nominal, String adminName) {
		contentSubject = "Transaksi Setor Tunai Anda Berhasil";
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
				"	  <td>Tanggal, waktu Transaksi</td>\r\n" + 
				"	  <td> : </td>\r\n" + 
				"	  <td> "+ tglTransaksi +" </td>\r\n" + 
				"	</tr>\r\n" + 
				"	<tr>\r\n" + 
				"	  <td>Jenis Transaksi</td>\r\n" + 
				"	  <td> : </td>\r\n" + 
				"	  <td> "+ jnsTransaksi +" </td>\r\n" + 
				"	</tr>\r\n" + 
				"	<tr>\r\n" + 
				"	  <td>No. Rekening Asal</td>\r\n" + 
				"	  <td> : </td>\r\n" + 
				"	  <td> " + noRekAsal+ "</td>\r\n" + 
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
				" Telah <strong>berhasil</strong> dilakukan. <br>\r\n" + 
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
				"<p style=\"color:Blue;font-family:'Lucida Sans Unicode';font-size:18px\"> "+ adminName +" </p>\r\n" + 
				"<p> Admin West Bank </p>\r\n" + 
				"\r\n" + 
				"<h2>WEST BANK</h2>\r\n" + 
				"<p>Jl. Slipi Jaya No. 1 Jakarta Barat</p>\r\n" + 
				"<p>   Telp : (021) 08123456789 </p>";
	}
	public void getContentFailedSetorTunai(int idTransaksi, Date tglTransaksi, String jnsTransaksi, String noRekAsal, String noRekTujuan, Double nominal, String adminName, String msg) {
		contentSubject = "Transaksi Setor Tunai Anda Gagal";
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
				"	  <td>Tanggal, waktu Transaksi</td>\r\n" + 
				"	  <td> : </td>\r\n" + 
				"	  <td> "+ tglTransaksi +" </td>\r\n" + 
				"	</tr>\r\n" + 
				"	<tr>\r\n" + 
				"	  <td>Jenis Transaksi</td>\r\n" + 
				"	  <td> : </td>\r\n" + 
				"	  <td> "+ jnsTransaksi +" </td>\r\n" + 
				"	</tr>\r\n" + 
				"	<tr>\r\n" + 
				"	  <td>No. Rekening Asal</td>\r\n" + 
				"	  <td> : </td>\r\n" + 
				"	  <td> " + noRekAsal+ "</td>\r\n" + 
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
				" Telah <strong>gagal</strong> dilakukan" + msg + " <br>\r\n" + 
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
				"<p style=\"color:Blue;font-family:'Lucida Sans Unicode';font-size:18px\"> "+ adminName +" </p>\r\n" + 
				"<p> Admin West Bank </p>\r\n" + 
				"\r\n" + 
				"<h2>WEST BANK</h2>\r\n" + 
				"<p>Jl. Slipi Jaya No. 1 Jakarta Barat</p>\r\n" + 
				"<p>   Telp : (021) 08123456789 </p>";
	}
}
