/**
 * Funzioni per la gestione delle finestre popup.
 */

// array delle finestre popup
var popupDlgs = [];

function apriFinestraLista(url, tipo)
{
  url = url.replace('pgm/template', 'rigel');

  var win = BootstrapDialog.show({
    title: tipo,
    size: BootstrapDialog.SIZE_WIDE,
    message: $('<div></div>').load(url),
    onhidden: function (dialogRef) {
      if (getTopDialog() == dialogRef) {
        // se chiudo senza selezionare, tolgo dallo stack la mia instanza
        popupDlgs.pop();
        // per debug
        // alert('DEBUG: instanze nello stack: ' + popupDlgs.length);
      }
    }
  });

  popupDlgs.push(win);
}

function apriFinestraForm(url, tipo)
{
  url = url.replace('pgm/template', 'rigel');

  var win = BootstrapDialog.show({
    title: tipo,
    size: BootstrapDialog.SIZE_WIDE,
    message: $('<div></div>').load(url),
    onhidden: function (dialogRef) {
      if (getTopDialog() == dialogRef) {
        // se chiudo senza selezionare, tolgo dallo stack la mia instanza
        popupDlgs.pop();
        // per debug
        // alert('DEBUG: instanze nello stack: ' + popupDlgs.length);
      }
    }
  });

  popupDlgs.push(win);
}

function getTopDialog()
{
  return popupDlgs[popupDlgs.length - 1];
}

function closeTopDialog()
{
  var win = popupDlgs.pop();
  win.close();
}

function reloadTopDialog(url)
{
  $("#rigel_dialog_body").load(url);
}

function setTopDialogTitle(titolo)
{
  getTopDialog().setTitle(titolo);
}

function jumpTopDialog(url)
{
  reloadTopDialog(url);
}

function impostaValori(func, codice, descrizione)
{
  window[func](codice, descrizione);
  closeTopDialog();
}

