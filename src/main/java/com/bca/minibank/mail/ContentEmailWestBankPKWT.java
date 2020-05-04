package com.bca.minibank.mail;

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
	
}
