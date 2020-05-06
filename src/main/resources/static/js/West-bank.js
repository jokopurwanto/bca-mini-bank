
function validateForm()
{
    if (
        validateName() &&
        validateKtp() &&
        dateValidation() &&
        validateNoTelp() &&
        validateEmail() &&
        validateUsername() &&
        validatePwd() &&
        checkPassword()
    ){
        alert('Terima kasih telah melakukan registrasi');
        return true;
    }
}

function validateEmail()
{
    var email = document.getElementById('email');
    var emailFormat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
    if (email.value.match(emailFormat)){
        return true;
    }
    alert("Alamat email tidak valid")
    email.focus();
    return false;
}

function validateKtp()
{
    var ktpNumber = document.getElementById('ktp');
    if (ktpNumber.value.length === 16){
        return true;
    }
    alert("KTP harus terdiri dari 16 digit")
    ktpNumber.focus();
    return false;
}

function validateNoTelp()
{
    var telponNumber = document.getElementById('hp');
    if (telponNumber.value.length >= 10 && telponNumber.value.length <= 14){
        return true;
    }
    alert("No Handphone terdiri dari minimum 10 dan maksimum 14 digit")
    telponNumber.focus();
    return false;
}
function validatePwd()
{
    var password = document.getElementById('pwd');
    // Minimum eight characters, at least one uppercase letter, one lowercase letter and one number:
    var pwdFormat = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/    ;

    if (password.value.match(pwdFormat)){
        return true;
    }
    alert("Password harus terdiri dari minimum 8 karakter serta kombinasi dari huruf besar, huruf kecil, dan angka")
    password.focus();
    return false;

}
function checkPassword()
{
    var password = document.getElementById('pwd').value;
    var confirmPwd = document.getElementById('confirm-pwd');

    if (confirmPwd.value === password){
        return true;
    }
    alert("Password tidak sama")
    confirmPwd.focus();
    return false;
}
function validateName()
{
    var name = document.getElementById('nama');
    var validasiHuruf = /^[a-zA-Z ]+$/;
    if (name.value.length <= 30 && name.value.match(validasiHuruf)){
        return true;
    }
    alert("Nama terdiri dari maksimum 30 karakter dan tidak mengandung angka")
    name.focus();
    return false;
}

function validateUsername()
{
    var username = document.getElementById('username');
    if (username.value.length >= 6){
        return true;
    }
    alert("Username terdiri dari minimum 6 karakter")
    username.focus();
    return false;
}

function dateValidation()
{
    var dateInput = moment(document.getElementById('date').value, 'YYYY-MM-DD');
    var dateNow = moment();
    var calculate = Math.abs(parseInt(dateNow.diff(dateInput, 'years', true)));

    if (calculate < 18){
        alert("Umur kurang dari 18 tahun");
        document.getElementById('date').focus();
        return false;
    }
    return true;
}

function dateFilter()
{
    var dateStart = moment(document.getElementById('dateStart').value, 'YYYY-MM-DD');
    var dateEnd = moment(document.getElementById('dateEnd').value, 'YYYY-MM-DD');
    var calculate = Math.abs(parseInt(dateEnd.diff(dateStart, 'days', true)));

    if (calculate > 30){
        alert("Rentang maksimum tanggal awal dan akhir adalah 30 hari");
        document.getElementById('dateEnd').focus();
        return false;
    }
    return true;    
}



function checkPassword2()
{
    var password = document.getElementById('pwd-new').value;
    var confirmPwd = document.getElementById('pwd-new2');

    if (confirmPwd.value === password){
        return true;
    }
    alert("Password tidak sama")
    confirmPwd.focus();
    return false;
}

function validatePwd2()
{
    var password = document.getElementById('pwd-new');
    // Minimum eight characters, at least one uppercase letter, one lowercase letter and one number:
    var pwdFormat = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/    ;

    if (password.value.match(pwdFormat)){
        return true;
    }
    alert("Password harus terdiri dari minimum 8 karakter serta kombinasi dari huruf besar, huruf kecil, dan angka")
    password.focus();
    return false;

}

function validateall()
{
	if(
			checkPassword2()&&
			validatePwd2() &&
	){
        alert('Password anda telah diubah');
        return true;
    }
}

function checkPin2()
{
    var pin = document.getElementById('pin-new').value;
    var confirmPin = document.getElementById('pin-new2');

    if (confirmPin.value === pin){
        return true;
    }
    alert("Pin tidak sama")
    confirmPin.focus();
    return false;
}

function validatePin2()
{
    var pin = document.getElementById('pin-new');
    // Minimum eight characters, at least one uppercase letter, one lowercase letter and one number:
    var pinFormat = /[0-9]{6,6}$/  ;

    if (pin.value.match(pinFormat)){
        return true;
    }
    alert("Pin harus 6 angka")
    pin.focus();
    return false;

}

function validateall2()
{
	if(
			checkPin2()&&
			validatePin2() &&
	){
        alert('Pin anda telah diubah');
        return true;
    }
}
