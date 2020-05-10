//dari Risyaf
//filter keyboard untuk nama, pin, username, no hp dan no telp
//Restricts input for the given textbox to the given inputFilter.
function setInputFilter(textbox, inputFilter) {
  ["input", "keydown", "keyup", "mousedown", "mouseup", "select", "contextmenu", "drop"].forEach(function(event) {
    textbox.addEventListener(event, function() {
      if (inputFilter(this.value)) {
        this.oldValue = this.value;
        this.oldSelectionStart = this.selectionStart;
        this.oldSelectionEnd = this.selectionEnd;
      } else if (this.hasOwnProperty("oldValue")) {
        this.value = this.oldValue;
        this.setSelectionRange(this.oldSelectionStart, this.oldSelectionEnd);
      } else {
        this.value = "";
      }
    });
  });
}


// Install input filters.
setInputFilter(document.getElementById("pin"), function(value) {
	return /^\d*$/.test(value); });
setInputFilter(document.getElementById("conf-pin"), function(value) {
	return /^\d*$/.test(value); });