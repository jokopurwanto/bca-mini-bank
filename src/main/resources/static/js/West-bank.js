//Validasi Form Registrasi
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

function validateName()
{
    var name = document.getElementById('nama');
    var validasiHuruf = /^[a-zA-Z.' ]+$/;
    if (name.value.length <= 30 && name.value.match(validasiHuruf)){
        return true;
    }
    alert("Nama terdiri dari maksimum 30 karakter dan tidak mengandung angka")
    name.focus();
    return false;
}

function validateKtp()
{
    var ktpNumber = document.getElementById('ktp');
    var validasiAngka = /^[0-9 ]+$/;
    if (ktpNumber.value.length === 16 && ktpNumber.value.match(validasiAngka)){
        return true;
    }
    alert("KTP harus terdiri dari 16 digit")
    ktpNumber.focus();
    return false;
}


function dateValidation()
{
    var dateInput = moment(document.getElementById('tgl-lahir').value, 'YYYY-MM-DD');
    var dateNow = moment();
    var calculate = Math.abs(parseInt(dateNow.diff(dateInput, 'years', true)));

    if (calculate < 17){
        alert("Umur kurang dari 17 tahun");
        document.getElementById('tgl-lahir').focus();
        return false;
    }
    return true;
}

function validateNoTelp()
{
    var telponNumber = document.getElementById('hp');
    if (telponNumber.value.length >= 10 && telponNumber.value.length <= 13){
        return true;
    }
    alert("No Handphone terdiri dari minimum 10 dan maksimum 13 digit")
    telponNumber.focus();
    return false;
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

function validateUsername()
{
    var username = document.getElementById('username');
    var validasiUserName = /^(?![0-9_.-])[A-Za-z0-9_.-]{6,12}$/;
    if (username.value.length >= 6 && username.value.match(validasiUserName)){
        return true;
    }
    alert("Username terdiri dari minimum 6 karakter dan tidak diawali dengan angka")
    username.focus();
    return false;
}

function validatePwd()
{
    var password = document.getElementById('pwd');
    var pwdFormat = /^(?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{8,32}$/    ;

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

//Validasi Filter Tanggal
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