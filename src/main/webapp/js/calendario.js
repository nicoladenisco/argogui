/**
 * Funzioni per la gestione del calendario.
 */

var calwindow = null;
var uriCalendario = null;

function apriCalendario(nomefunzione)
{
  calwindow = openCalendarWindow(
          uriCalendario + '?mode=single&func=' + nomefunzione);
}

function apriCalendarioIntervallo(nomefunzione, nomefunIntervallo)
{
  calwindow = openCalendarWindow(
          uriCalendario + '?mode=interval&func=' + nomefunzione + '&fint=' + nomefunIntervallo);
}

function apriCalendarioForm(nomeform, nomefunzione)
{
  calwindow = openCalendarWindow(
          uriCalendario + '?mode=single&func=' + nomefunzione + '&form=' + nomeform);
}

function apriCalendarioIntervalloForm(nomeform, nomefunzione, nomefunIntervallo)
{
  calwindow = openCalendarWindow(
          uriCalendario + '?mode=interval&func=' + nomefunzione + '&fint=' + nomefunIntervallo + '&form=' + nomeform);
}

function setUriCalendario(uri)
{
  uriCalendario = uri;
}

function openCalendarWindow(urlwin)
{
  return BootstrapDialog.show({
    title: 'Calendario',
    message: $('<div></div>').load(urlwin)
  });
}

function changeMonth() {
  document.calendario.month.value = document.calendario.LM.options[document.calendario.LM.selectedIndex].value + '';
  reloadCalendario();
}

function changeYear() {
  document.calendario.year.value = document.calendario.LY.options[document.calendario.LY.selectedIndex].value + '';
  reloadCalendario();
}

function goDay(anno, mese, giorno) {
  document.calendario.day.value = giorno;
  document.calendario.month.value = mese;
  document.calendario.year.value = anno;
  reloadCalendario();
}

function reloadCalendario()
{
  var url = uriCalendario + "?";
  url += "day=" + document.calendario.day.value + "&";
  url += "month=" + document.calendario.month.value + "&";
  url += "year=" + document.calendario.year.value;

  $("#calbody").load(url);
}

function chiudiCalendario()
{
  calwindow.close();
}

function changeDay(func, day)
{
  window[func](day);
  calwindow.close();
}
