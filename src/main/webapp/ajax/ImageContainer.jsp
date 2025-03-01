<%@page import="org.argogui.services.dcmsrv.InstanceResultBean"%>
<%@page import="org.argogui.utils.SU"%>
<%@page import="org.argogui.beans.VisContext"%>
<%@page import="org.argogui.beans.VisualizzatoreBean"%>
<%@page import="org.sirio6.beans.BeanFactory"%>
<%@page import="org.sirio6.utils.CoreRunData"%>
<%@page import="org.sirio6.beans.BeanFactory"%>
<%@page import="com.pixelmed.dicom.TagFromName"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page import="com.pixelmed.dicom.SOPClass"%>
<%@page import="org.apache.turbine.services.rundata.RunDataService"%>


<%
  // Placeholder for the RunData object.
  CoreRunData data = null;

  try
  {
    // Get general RunData here...
    // Perform turbine specific initialization below.
    data = (CoreRunData) TurbineRunDataFacade.getRunData(request, response, getServletConfig());
    VisualizzatoreBean bean = BeanFactory.getFromSession(data, VisualizzatoreBean.class);
    VisContext ctx = bean.getOrAddStudy(data);

    String serieUID = SU.okStrNull(data.getParameters().getString("serieUID"));
    if(serieUID != null)
      ctx.setCurrSerie(serieUID);

    // ricarica il serieUID dall'effettiva serie corrente
    serieUID = ctx.currSerie.SerieInstanceUID;

    String studyUID = ctx.studyUID;
    String imageUID = ctx.currInstance.SOPInstanceUID;
    String modality = ctx.currSerie.modalita;
    String sopClass = ctx.currInstance.SOPClassUID;
    boolean multiFrame = false;
    int numberOfFrames = 0;
    String uriImage1 = data.getContextPath() + "/Image.do?study=" + studyUID;
    String uriImage2 = uriImage1 + "&series=" + serieUID;
    String uriImage3 = uriImage2 + "&object=" + imageUID;

    pageContext.setAttribute("studyUID", studyUID);
    pageContext.setAttribute("serieUID", serieUID);
    pageContext.setAttribute("imageUID", imageUID);
    pageContext.setAttribute("modality", modality);
    pageContext.setAttribute("sopClass", sopClass);
    pageContext.setAttribute("multiFrame", multiFrame);
    pageContext.setAttribute("numberOfFrames", numberOfFrames);
    pageContext.setAttribute("uriImage1", uriImage1);
    pageContext.setAttribute("uriImage2", uriImage2);
    pageContext.setAttribute("uriImage3", uriImage3);

    pageContext.setAttribute("patientID", ctx.getTagAsString(TagFromName.PatientID));
    //pageContext.setAttribute("studyUID", ctx.res.StudyInstanceUID);
    //pageContext.setAttribute("seriesUID", ctx.currSerie.SerieInstanceUID);
    //pageContext.setAttribute("instanceUID", ctx.currInstance.SOPInstanceUID);
    //pageContext.setAttribute("sopclassUID", ctx.currInstance.SOPClassUID);
    //pageContext.setAttribute("modality", ctx.currSerie.modalita);
    pageContext.setAttribute("numberOfImages", ctx.currSerie.arFiles.size());
    pageContext.setAttribute("patientNameEscape", ctx.getTagAsString(TagFromName.PatientName));
    pageContext.setAttribute("totalNoOfStudies", "1");
    pageContext.setAttribute("studyDesc", ctx.getTagAsString(TagFromName.StudyDescription));
    pageContext.setAttribute("patientSex", ctx.getTagAsString(TagFromName.PatientSex));
    pageContext.setAttribute("physicianNameHtml", ctx.getTagAsString(TagFromName.ReferringPhysicianName));
    pageContext.setAttribute("patientBirthDate", ctx.getTagAsString(TagFromName.PatientBirthDate));
    pageContext.setAttribute("studyDate", ctx.getTagAsString(TagFromName.StudyDate));
    pageContext.setAttribute("modalities", ctx.res.modalita);
    pageContext.setAttribute("rows", 128);
%>

<div id="left">
  <c:choose>
    <c:when test="${modality =='SR' }">
      <div class="shadow" id="patStudyDesc" style="visibility:hidden;"></div>
      <div id="SRContent"></div>
    </c:when>

    <c:when test="${modality =='KO' }">
      <div class="shadow" id="patStudyDesc" style="visibility:hidden;"></div>
      <div id="KOContent"></div>
    </c:when>

    <c:when test="${modality == 'ES' && sopClass == '1.2.840.10008.5.1.4.1.1.77.1.1.1' }">
      <div class="shadow" id="patStudyDesc" style="visibility:hidden;"></div>
      <div id="MPEGContent"></div>
      <img alt="" class="dragme" name="picture" src="" width="0" id="picture">
    </c:when>

    <c:when test="${sopClass == '1.2.840.10008.5.1.4.1.1.104.1' }">
      <div id="PDFContent">
        <OBJECT id="pdf" type="application/pdf" data="${uriImage3}" width=700 height=700>
          PDF Plugin not found. Please install PDF Plugin and try again.
        </OBJECT>
      </div>
    </c:when>

    <c:when test="${frames =='yes' && numberOfFrames > 1 }">
      <div class="shadow" id="patStudyDesc"></div>
      <div class="shadow" id="patSex">${patientSex}</div>
      <div class="shadow" onclick=" even = document.getElementById('picture').event;
          initwl();" id="patPhyName">${physicianNameHtml}</div>
      <div class="shadow" id="patBirthDate">${patientBirthDate}</div>
      <div class="shadow" id="patStudyDate">${studyDates}</div>
      <div class="shadow" id="patModality">${modality}</div>

      <div class="shadow" id="windowLevel"></div>
      <div class="shadow" id="pixelSpacing"></div>
      <div class="shadow" id="pixelMessage"></div>
      <div class="shadow" id="nativeRes"></div>

      <div class="shadow" id="number">Frame 1 of ${numberOfFrames}</div>
      <table id="imgTable">
        <tr>
          <td class="imageHolder">
            <div id = "imageHolder" class="imageHolder">
              <center>
                <img alt="" class="dragme" name="picture" src="${uriImage3}" width="512" id="picture">
              </center>
            </div>
          <td>
        </tr>
      </table>

    </c:when>

    <c:otherwise>
      <div class="shadow" id="patStudyDesc"></div>
      <div class="shadow" id="patSex">${patientSex}</div>
      <div class="shadow" onclick=" even = document.getElementById('picture').event;
          initwl();" id="patPhyName">${physicianNameHtml}</div>
      <div class="shadow" id="patBirthDate">${patientBirthDate}</div>
      <div class="shadow" id="patStudyDate">${studyDates}</div>
      <div class="shadow" id="patModality">${modality}</div>

      <div class="shadow" id="windowLevel"></div>
      <div class="shadow" id="pixelSpacing"></div>
      <div class="shadow" id="pixelMessage"></div>
      <div class="shadow" id="nativeRes"></div>

      <div class="shadow" id="full_resolution" style="visibility:hidden;" onclick="showFullResolution();">&nbsp;View Full Resolution&nbsp;</div>

      <div class="shadow" id="number">Image 1 of ${numberOfImages}</div>
      <table id="imgTable">
        <tr>
          <td class="imageHolder">
            <div id = "imageHolder" class="imageHolder">
              <img style="display:none;" alt="" class="dragme" name="picture" src="" width="512" id="picture"></img>
            </div>
          <td>
        </tr>
      </table>

    </c:otherwise>
  </c:choose>

</div>


<%--//ThumbNails Div to hold the thumbDivider and thumbNailHolder --%>
<div id="thumbNails" class='flexcroll'>

  <%--thumbDivider to separate the image and thumbnails --%>
  <div id="thumbDivider" onclick="singleImage();" title="Click or press 'i' to toggle thumbnails visibility">
    <img id="dividerImg" src="images/icn_grip1.gif" alt="">
  </div><%--End of thumbDivider --%>

  <div id="imageCacheHolder" style="display:none">
  </div>

  <%-- thumbNailHolder Div to contain the thumbnails of the selected Series. --%>
  <div id="thumbNailHolder">

    <%
      int c = 0;
      for(InstanceResultBean img : ctx.currSerie.arInstances)
      {
        pageContext.setAttribute("img", c);
        pageContext.setAttribute("instanceNumber", img.getNum());
        pageContext.setAttribute("imageId", img.SOPInstanceUID);
        pageContext.setAttribute("sopClassUID", img.SOPClassUID);
    %>

    <div class="scale-item">
      <div class="imgNo">${instanceNumber}</div>
      <c:choose>
        <c:when test="${modality =='SR' }">
          <img alt="" id="img${img}" name="images/icons/SR_Latest.png" class="scale-image" src="images/icons/SR_Latest.png" width="100%" onclick="ajaxpage('SRContent', '${uriImage2}&object=${imageId}&contentType=text/html');
              $('SRContent').style.color = '#000000';
              changeBorder(this);
              return false;">
        </c:when>

        <c:when test="${modality =='KO' }">
          <img alt="" id="img${img}" name="images/icons/KO.png" class="scale-image" src="images/icons/KO.png" width="100%" onclick="ajaxpage('KOContent', '${uriImage2}&object=${imageId}&contentType=text/html');
              $('KOContent').style.color = '#000000';
              changeBorder(this);
              return false;">
        </c:when>

        <c:when test="${modality == 'ES' && sopClassUID == '1.2.840.10008.5.1.4.1.1.77.1.1.1' }">
          <img alt="" id="img${img}" name="images/icons/icn_video.gif" class="scale-image" src="images/icons/icn_video.gif" width="100%" onclick="vlc_controls = null;
              init_vlc_player();
              vlc_controls.play('wado?requestType=WADO&contentType=application/dicom&studyUID=${studyUID}&seriesUID=${serieUID}&objectUID=${imageId}');
              changeBorder(this);
              return false;" onload="loadStudyDesc('${studyDesc}');">
        </c:when>

        <c:when test="${sopClassUID =='1.2.840.10008.5.1.4.1.1.104.1' }">
          <img alt="" id="img${img}" name="images/icons/PDF.png" class="scale-image" src="images/icons/PDF.png" width="100%" onclick="$(pdf).data = 'Image.do?study=${studyUID}&series=${serieUID}&object=${imageId}';
              changeBorder(this);
               ">
        </c:when>

        <c:otherwise>
          <c:choose>
            <c:when test="${frames =='yes' && numberOfFrames > 1 }">
              <img alt="" id="img${img}" name="${uriImage2}&object=${imageId}" class="scale-image measurable" src="images/icons/filler_black.jpg" width="100%" onclick="fti = 0;
                  multiFrames = true;
                  changeSpeed1(ajaxpage('', 'MFrames?datasetURL=http://${applicationScope.serverConfig.hostName}:${applicationScope.serverConfig.wadoPort}/wado?requestType=WADO&contentType=application/dicom&studyUID=${studyUID}&seriesUID=${seriesId}&objectUID=${imageId}'));
                  setImageInfos('${numberOfFrames}');
                  if (cineloop)
                    cineLoop();
                  loadImageCache('${uriImage2}&object=${imageId}&rows=${rows}&frameNumber=',${numberOfFrames});
                  setImageAndHeaders('${uriImage2}&object=${imageId}&rows=${rows}&frameNumber=0');
                  cineLoop();
                  changeBorder(this);
                  selectedInstanceIndex = ${img};
                  changeDataset();" onload="loadStudyDesc('${studyDesc}');">
            </c:when>
            <c:otherwise>
              <img alt="" id="img${img}"
                   src="${uriImage2}&object=${imageId}"
                   class="scale-image measurable"
                   name="images/icons/filler_black.jpg"
                   width="100%"
                   onclick="if (cineloop)
                         cineLoop();
                       setImageInfos('${numberOfImages}');
                       changeslides(${img});
                       changeBorder(this);
                       changeDataset();"

                   onload = "checkResolution(${rows});
                       loadStudyDesc('${studyDesc}');">
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose>
    </div>

    <%
        c++;
      }
    %>

  </div><%--End of thumbNailHolder Div --%>
</div><%--End of thumbNails Div --%>

<%
  }
  finally
  {
    // Return the used RunData to the factory for recycling.
    if(data != null)
      TurbineRunDataFacade.putRunData(data);
  }
%>


