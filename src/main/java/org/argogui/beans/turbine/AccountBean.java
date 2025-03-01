/*
 *  Copyright (C) 2016 RAD-IMAGE s.r.l.
 *
 *  Questo software è proprietà di RAD-IMAGE s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  RAD-IMAGE s.r.l.
 *  Via San Giovanni, 1 - Contrada Belvedere
 *  San Nicola Manfredi (BN)
 *
 *  Creato il 10 Febbraio 2016, 19:06:00
 */
package org.argogui.beans.turbine;

import org.argogui.beans.ArgoBaseBean;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.criteria.SqlEnum;
import org.apache.torque.util.Transaction;
import org.apache.turbine.util.RunData;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.argogui.om.TurbineGroup;
import org.argogui.om.TurbineGroupPeer;
import org.argogui.om.TurbinePermission;
import org.argogui.om.TurbinePermissionPeer;
import org.argogui.om.TurbineRole;
import org.argogui.om.TurbineRolePeer;
import org.argogui.om.TurbineRolePermission;
import org.argogui.om.TurbineRolePermissionPeer;
import org.argogui.om.TurbineUser;
import org.argogui.om.TurbineUserGroupRole;
import org.argogui.om.TurbineUserGroupRolePeer;
import org.argogui.om.TurbineUserPeer;
import org.argogui.services.SERVICE;
import org.argogui.utils.SU;
import org.rigel5.RigelI18nInterface;
import org.rigel5.db.DbUtils;
import org.sirio6.ErrorMessageException;
import org.sirio6.rigel.RigelHtmlI18n;
import org.sirio6.services.security.SEC;
import org.sirio6.utils.CoreRunData;
import org.sirio6.utils.LI;

/**
 * Bean per la gestione degli account in generale.
 *
 * FILENOI18N
 * @author Nicola De Nisco
 */
@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
public class AccountBean extends ArgoBaseBean
{
  public boolean authDelete = false;
  public boolean authDeletePerm = false;

  public static final String PAGE_ONEUTE_RUOLI = "AccountURuoli.vm";
  public static final String PAGE_ONEROLE_USERS = "AccountRUtenti.vm";
  public static final String PAGE_ONEPERMISSION_RUOLI = "AccountPRuoli.vm";
  public static final String PAGE_ONEROLE_PERMISSION = "AccountRPermission.vm";

  protected String sRowHeader = "TR class=\"rigel_table_header_row\"";
  protected String sColHeader = "TD class=\"rigel_table_header_cell\"";
  protected String GLOBAL_GROUP_NAME = "global";
  protected String butSi
     = "<a href=\"#\" onclick=\"%s\"><i class=\"fa fa-plus-square fa-fw fa-lg\" aria-hidden=\"true\"></i></a>";
  protected String butNo
     = "<a href=\"#\" onclick=\"%s\"><i class=\"fa fa-minus-square fa-fw fa-lg\" aria-hidden=\"true\"></i></a>";

  protected int groupId = 0;
  protected RigelI18nInterface i18n = null;

  protected String locUtenti, locRuoli, locPermessi;

  @Override
  public void init(CoreRunData data)
     throws Exception
  {
    super.init(data);
    i18n = new RigelHtmlI18n(data);
    locUtenti = i18n.msg("Utenti");
    locRuoli = i18n.msg("Ruoli");
    locPermessi = i18n.msg("Permessi");

    if(SEC.checkAllPermission(data, "admin_users"))
      authDelete = true;

    if(SEC.isAdmin(data))
      authDeletePerm = true;

    leggiDati();
  }

  @Override
  public boolean isValid(CoreRunData data)
  {
    int reload = data.getParameters().getInt("reload");

    if(reload > 0)
      return false;

    return super.isValid(data);
  }

  protected String drawButSI(String script)
  {
    return String.format(butSi, script);
  }

  protected String drawButNO(String script)
  {
    return String.format(butNo, script);
  }

  ////////////////////////////////////////////////////////////////////////
  // UTENTI/RUOLI
  public List vUser, vGroup, vRole, vPerm, vUserGroupRole, vRolePerm;
  public int[] uidSelected = null;
  public int[] ridSelected = null;
  public int[] pidSelected = null;
  public int[] tidSelected = null;

  protected void leggiDati()
     throws Exception
  {
    vUser = TurbineUserPeer.doSelect((new Criteria()).addAscendingOrderByColumn(TurbineUserPeer.LOGIN_NAME));

    Criteria cGroup = new Criteria();
    //cGroup.add(TurbineGroupPeer.GROUP_ID, 0, SqlEnum.GREATER_THAN);
    cGroup.addAscendingOrderByColumn(TurbineGroupPeer.GROUP_NAME);
    vGroup = TurbineGroupPeer.doSelect(cGroup);

    // imposta il gruppo corrente a quello globale
    groupId = -1;
    for(int i = 0; i < vGroup.size(); i++)
    {
      TurbineGroup tg = (TurbineGroup) vGroup.get(i);
      if(SU.isEqu(GLOBAL_GROUP_NAME, tg.getName()))
        groupId = tg.getEntityId();
    }
    SERVICE.ASSERT(groupId != -1, "groupId != -1");

    Criteria cRole = new Criteria();
    cRole.and(TurbineRolePeer.ROLE_ID, 0, SqlEnum.GREATER_THAN);
    cRole.addAscendingOrderByColumn(TurbineRolePeer.ROLE_NAME);
    vRole = TurbineRolePeer.doSelect(cRole);

    Criteria cPerm = new Criteria();
    cPerm.and(TurbinePermissionPeer.PERMISSION_ID, 0, SqlEnum.GREATER_THAN);
    cPerm.addAscendingOrderByColumn(TurbinePermissionPeer.PERMISSION_NAME);
    vPerm = TurbinePermissionPeer.doSelect(cPerm);

    vUserGroupRole = TurbineUserGroupRolePeer.doSelect(new Criteria());
    vRolePerm = TurbineRolePermissionPeer.doSelect(new Criteria());
  }

  private boolean checkUsrGrpRole(int userId, int groupId, int roleId)
  {
    for(int i = 0; i < vUserGroupRole.size(); i++)
    {
      TurbineUserGroupRole ugr = (TurbineUserGroupRole) (vUserGroupRole.get(i));
      if(ugr.getUserId() == userId
         && ugr.getGroupId() == groupId
         && ugr.getRoleId() == roleId)
        return true;
    }
    return false;
  }

  public boolean isSelectedUser(TurbineUser tu)
  {
    return isSelectedUser(tu.getEntityId());
  }

  public boolean isSelectedUser(int idUser)
  {
    if(uidSelected == null)
      return false;

    for(int i = 0; i < uidSelected.length; i++)
    {
      if(idUser == uidSelected[i])
        return true;
    }

    return false;
  }

  public boolean isSelectedRole(TurbineRole tr)
  {
    return isSelectedRole(tr.getEntityId());
  }

  public boolean isSelectedRole(int idRole)
  {
    if(ridSelected == null)
      return false;

    for(int i = 0; i < ridSelected.length; i++)
    {
      if(idRole == ridSelected[i])
        return true;
    }

    return false;
  }

  public boolean isSelectedPermission(TurbinePermission tp)
  {
    return isSelectedPermission(tp.getEntityId());
  }

  public boolean isSelectedPermission(int idPermission)
  {
    if(pidSelected == null)
      return false;

    for(int i = 0; i < pidSelected.length; i++)
    {
      if(idPermission == pidSelected[i])
        return true;
    }

    return false;
  }

  public boolean isSelectedPRole(TurbineRole tr)
  {
    return isSelectedPRole(tr.getEntityId());
  }

  public boolean isSelectedPRole(int idRole)
  {
    if(tidSelected == null)
      return false;

    for(int i = 0; i < tidSelected.length; i++)
    {
      if(idRole == tidSelected[i])
        return true;
    }

    return false;
  }

  /**
   * Ritorna l'HTML per l'editing dei delle associazioni Utenti->Ruoli.
   * @return html
   * @throws java.lang.Exception
   */
  public String getHtmlTedit5()
     throws Exception
  {
    StringBuilder sOut = new StringBuilder(8192);
    sOut.append(
       "<div class='rigel_formtable'>"
       + "<" + tagTabelleForm + ">\r\n"
       + "<thead>"
       + "<" + sRowHeader + ">"
       + "<" + sColHeader + " colspan=2 rowspan=2 align=center>" + locUtenti + "</td>"
       + "<" + sColHeader + " colspan=" + (vRole.size()) + " align=center>" + locRuoli + "</td>"
       + "</tr>\r\n");

    String baseUrlURuoli = LI.getLinkUrl(PAGE_ONEUTE_RUOLI) + "?userid=";
    String baseUrlRUsers = LI.getLinkUrl(PAGE_ONEROLE_USERS) + "?roleid=";

    sOut.append("<tr>");
    for(int r = 0; r < vRole.size(); r++)
    {
      TurbineRole tr = (TurbineRole) (vRole.get(r));
      sOut.append("<td><a href=\"" + baseUrlRUsers + tr.getEntityId() + "\">" + tr.getName() + "</a></td>");
    }
    sOut.append("</tr>");
    sOut.append("</thead>");

    sOut.append("<tbody>");
    for(int u = 0; u < vUser.size(); u++)
    {
      TurbineUser us = (TurbineUser) (vUser.get(u));
      int userId = us.getEntityId();

      sOut.append("<tr>");
      sOut.append("<td><a href=\"" + baseUrlURuoli + us.getEntityId() + "\">" + us.getEntityName() + "</a></td>");
      sOut.append("<td align=right>" + drawButSI("tuttiSI(" + userId + ")"));
      sOut.append(drawButNO("tuttiNO(" + userId + ")") + "</td>");

      for(int r = 0; r < vRole.size(); r++)
      {
        TurbineRole tr = (TurbineRole) (vRole.get(r));
        int roleId = tr.getEntityId();
        String cbName = "RUGR_" + userId + "_" + roleId;
        String hidName = "HUGR_" + userId + "_" + roleId;

        sOut.append(
           "<td><input type=\"checkbox\" name=\"" + cbName + "\" value=\"1\" "
           + (checkUsrGrpRole(userId, groupId, roleId) ? "checked" : "") + ">"
           + "<input type=\"hidden\" name=\"" + hidName + "\" value=\"1\"></td>\n");
      }

      sOut.append("</tr>");
    }

    sOut.append("<tr><td colspan=2>&nbsp;</td>");
    for(int r = 0; r < vRole.size(); r++)
    {
      TurbineRole tr = (TurbineRole) (vRole.get(r));
      int roleId = tr.getEntityId();
      sOut.append("<td align=left>" + drawButSI("tuttiRoleSI(" + roleId + ")"));
      sOut.append(drawButNO("tuttiRoleNO(" + roleId + ")") + "</td>");
    }
    sOut.append("</tr>");
    sOut.append("</tbody>");

    sOut.append("</table></div>");
    return sOut.toString();
  }

  /**
   * Ritorna l'HTML per l'editing dei delle associazioni Utenti->Ruoli.
   * @param idUser
   * @return html
   * @throws java.lang.Exception
   */
  public String getHtmlTedit5OneUser(int idUser)
     throws Exception
  {
    uidSelected = new int[]
    {
      idUser
    };
    return getHtmlTedit5SelectedUsers();
  }

  /**
   * Ritorna l'HTML per l'editing dei delle associazioni Utenti->Ruoli.
   * @return html
   * @throws java.lang.Exception
   */
  public String getHtmlTedit5SelectedUsers()
     throws Exception
  {
    StringBuilder sOut = new StringBuilder(8192);
    sOut.append(
       "<div class='rigel_formtable'>"
       + "<" + tagTabelleForm + ">\r\n"
       + "<" + sRowHeader + ">"
       + "<" + sColHeader + " colspan=2 rowspan=2 align=center>" + locRuoli + "</td>"
       + "<" + sColHeader + " colspan=" + (uidSelected.length) + " align=center>" + locUtenti + "</td>"
       + "</tr>\r\n");

    String baseUrlRUsers = LI.getLinkUrl(PAGE_ONEROLE_USERS) + "?roleid=";

    sOut.append("<tr>");
    for(int i = 0; i < uidSelected.length; i++)
    {
      int idUser = uidSelected[i];
      TurbineUser us = getUser(idUser);
      sOut.append("<td>" + us.getEntityName() + "</td>");
    }
    sOut.append("</tr>");

    for(int r = 0; r < vRole.size(); r++)
    {
      TurbineRole tr = (TurbineRole) (vRole.get(r));
      int roleId = tr.getEntityId();
      sOut.append("<tr>");
      sOut.append("<td><a href=\"" + baseUrlRUsers + tr.getEntityId() + "\">" + tr.getName() + "</a></td>\n");
      sOut.append("<td align=right>" + drawButSI("tuttiRoleSI(" + roleId + ")"));
      sOut.append(drawButNO("tuttiRoleNO(" + roleId + ")") + "</td>");

      for(int i = 0; i < uidSelected.length; i++)
      {
        int idUser = uidSelected[i];
        String cbName = "RUGR_" + idUser + "_" + roleId;
        String hidName = "HUGR_" + idUser + "_" + roleId;

        sOut.append(
           "<td><input type=\"checkbox\" name=\"" + cbName + "\" value=\"1\" "
           + (checkUsrGrpRole(idUser, groupId, roleId) ? "checked" : "") + ">"
           + "<input type=\"hidden\" name=\"" + hidName + "\" value=\"1\"></td>\n");
      }
      sOut.append("</tr>");
    }

    sOut.append("<tr><td colspan=2>&nbsp;</td>");
    for(int i = 0; i < uidSelected.length; i++)
    {
      int idUser = uidSelected[i];
      sOut.append("<td align=left>" + drawButSI("tuttiSI(" + idUser + ")"));
      sOut.append(drawButNO("tuttiNO(" + idUser + ")") + "</td>");
    }
    sOut.append("</tr>");

    sOut.append("</table></div>\r\n");
    return sOut.toString();
  }

  /**
   * Ritorna l'HTML per l'editing dei delle associazioni Utenti->Ruoli.
   * @param idRole
   * @return html
   * @throws java.lang.Exception
   */
  public String getHtmlTedit5OneRole(int idRole)
     throws Exception
  {
    ridSelected = new int[]
    {
      idRole
    };
    return getHtmlTedit5SelectedRoles();
  }

  /**
   * Ritorna l'HTML per l'editing dei delle associazioni Utenti->Ruoli.
   * @return html
   * @throws java.lang.Exception
   */
  public String getHtmlTedit5SelectedRoles()
     throws Exception
  {
    StringBuilder sOut = new StringBuilder(8192);
    sOut.append(
       "<div class='rigel_formtable'>"
       + "<" + tagTabelleForm + ">\r\n"
       + "<" + sRowHeader + ">"
       + "<" + sColHeader + " colspan=2 rowspan=2 align=center>" + locUtenti + "</td>"
       + "<" + sColHeader + " colspan=" + (ridSelected.length) + " align=center>" + locRuoli + "</td>"
       + "</tr>\r\n");

    String baseUrlURuoli = LI.getLinkUrl(PAGE_ONEUTE_RUOLI) + "?userid=";

    sOut.append("<tr>");
    for(int i = 0; i < ridSelected.length; i++)
    {
      int idRole = ridSelected[i];
      TurbineRole tr = getRole(idRole);
      sOut.append("<td>" + tr.getName() + "</td>");
    }
    sOut.append("</tr>");

    for(int u = 0; u < vUser.size(); u++)
    {
      TurbineUser us = (TurbineUser) (vUser.get(u));
      int userId = us.getEntityId();

      sOut.append("<tr>");
      sOut.append("<td><a href=\"" + baseUrlURuoli + us.getEntityId() + "\">" + us.getEntityName() + "</a></td>");
      sOut.append("<td align=right>" + drawButSI("tuttiSI(" + userId + ")"));
      sOut.append(drawButNO("tuttiNO(" + userId + ")") + "</td>");

      for(int i = 0; i < ridSelected.length; i++)
      {
        int idRole = ridSelected[i];
        String cbName = "RUGR_" + userId + "_" + idRole;
        String hidName = "HUGR_" + userId + "_" + idRole;

        sOut.append(
           "<td><input type=\"checkbox\" name=\"" + cbName + "\" value=\"1\" "
           + (checkUsrGrpRole(userId, groupId, idRole) ? "checked" : "") + ">"
           + "<input type=\"hidden\" name=\"" + hidName + "\" value=\"1\"></td>\n");
      }

      sOut.append("</tr>");
    }

    sOut.append("<tr><td colspan=2>&nbsp;</td>");
    for(int i = 0; i < ridSelected.length; i++)
    {
      int idRole = ridSelected[i];
      sOut.append("<td align=left>" + drawButSI("tuttiRoleSI(" + idRole + ")"));
      sOut.append(drawButNO("tuttiRoleNO(" + idRole + ")") + "</td>");
    }
    sOut.append("</tr>");

    sOut.append("</table></div>\r\n");
    return sOut.toString();
  }

  private boolean checkRolePerm(int roleId, int permId)
  {
    for(int i = 0; i < vRolePerm.size(); i++)
    {
      TurbineRolePermission ugr = (TurbineRolePermission) (vRolePerm.get(i));
      if(ugr.getRoleId() == roleId && ugr.getPermissionId() == permId)
        return true;
    }
    return false;
  }

  public TurbineGroup getGroup(int idGroup)
  {
    for(int u = 0; u < vGroup.size(); u++)
    {
      TurbineGroup gp = (TurbineGroup) vGroup.get(u);
      if(gp.getEntityId() == idGroup)
        return gp;
    }
    return null;
  }

  public TurbineGroup findGroup(String groupName)
  {
    for(int u = 0; u < vGroup.size(); u++)
    {
      TurbineGroup gp = (TurbineGroup) vGroup.get(u);
      if(SU.isEqu(groupName, gp.getName()))
        return gp;
    }
    return null;
  }

  public TurbineUser getUser(int idUser)
  {
    for(int u = 0; u < vUser.size(); u++)
    {
      TurbineUser us = (TurbineUser) (vUser.get(u));
      if(us.getEntityId() == idUser)
        return us;
    }
    return null;
  }

  public TurbineUser findUser(String loginName)
  {
    for(int u = 0; u < vUser.size(); u++)
    {
      TurbineUser us = (TurbineUser) (vUser.get(u));
      if(SU.isEqu(loginName, us.getEntityName()))
        return us;
    }
    return null;
  }

  public TurbineRole getRole(int idRole)
  {
    for(int r = 0; r < vRole.size(); r++)
    {
      TurbineRole tr = (TurbineRole) (vRole.get(r));
      if(tr.getEntityId() == idRole)
        return tr;
    }
    return null;
  }

  public TurbinePermission getPermission(int idPermission)
  {
    for(int i = 0; i < vPerm.size(); i++)
    {
      TurbinePermission tp = (TurbinePermission) (vPerm.get(i));
      if(tp.getEntityId() == idPermission)
        return tp;
    }
    return null;
  }

  public TurbineRole findRole(String roleName)
  {
    for(int r = 0; r < vRole.size(); r++)
    {
      TurbineRole tr = (TurbineRole) (vRole.get(r));
      if(SU.isEqu(roleName, tr.getName()))
        return tr;
    }
    return null;
  }

  public TurbinePermission findPermission(String permissionName)
  {
    for(int i = 0; i < vPerm.size(); i++)
    {
      TurbinePermission tp = (TurbinePermission) (vPerm.get(i));
      if(SU.isEqu(permissionName, tp.getName()))
        return tp;
    }
    return null;
  }

  /**
   * Ritorna HTML per l'editing delle associazioni Ruoli->Permessi.
   * @return html
   * @throws java.lang.Exception
   */
  public String getHtmlTedit6()
     throws Exception
  {
    StringBuilder sOut = new StringBuilder(8192);
    sOut.append(
       "<div class='rigel_formtable'>"
       + "<" + tagTabelleForm + ">\r\n"
       + "<" + sRowHeader + ">"
       + "<" + sColHeader + " colspan=2 rowspan=2 align=center>" + locPermessi + "</td>"
       + "<" + sColHeader + " colspan=" + (vRole.size()) + " align=center>" + locRuoli + "</td>"
       + "</tr>\r\n");

    String baseUrlPRuoli = LI.getLinkUrl(PAGE_ONEPERMISSION_RUOLI) + "?permid=";
    String baseUrlRPerms = LI.getLinkUrl(PAGE_ONEROLE_PERMISSION) + "?roleid=";

    sOut.append("<tr>");
    Iterator iterRoleH = vRole.iterator();
    while(iterRoleH.hasNext())
    {
      TurbineRole r = (TurbineRole) (iterRoleH.next());
      sOut.append("<td><a href=\"" + baseUrlRPerms + r.getEntityId() + "\">" + r.getName() + "</a></td>");
    }
    sOut.append("</tr>");

    for(int p = 0; p < vPerm.size(); p++)
    {
      TurbinePermission tp = (TurbinePermission) (vPerm.get(p));
      int permId = tp.getEntityId();

      sOut.append("<tr>");
      sOut.append("<td align=left><a href=\"" + baseUrlPRuoli + tp.getEntityId() + "\">" + tp.getName() + "</a></td>");
      sOut.append("<td align=right>" + drawButSI("tuttiSI(" + permId + ")"));
      sOut.append(drawButNO("tuttiNO(" + permId + ")") + "</td>");

      for(int r = 0; r < vRole.size(); r++)
      {
        TurbineRole tr = (TurbineRole) (vRole.get(r));
        int roleId = tr.getEntityId();
        String cbName = "RPC_" + permId + "_" + roleId;
        String hidName = "HRPC_" + permId + "_" + roleId;

        sOut.append(
           "<td><input type=\"checkbox\" name=\"" + cbName + "\" value=\"1\" "
           + (checkRolePerm(roleId, permId) ? "checked" : "") + ">"
           + "<input type=\"hidden\" name=\"" + hidName + "\" value=\"1\" ></td>\n");
      }

      sOut.append("</tr>");
    }

    sOut.append("<tr><td colspan=2>&nbsp;</td>");
    for(int r = 0; r < vRole.size(); r++)
    {
      TurbineRole tr = (TurbineRole) (vRole.get(r));
      int roleId = tr.getEntityId();
      sOut.append("<td align=left>" + drawButSI("tuttiRoleSI(" + roleId + ")"));
      sOut.append(drawButNO("tuttiRoleNO(" + roleId + ")") + "</td>");
    }
    sOut.append("</tr>");

    sOut.append("</table></div>");
    return sOut.toString();
  }

  /**
   * Ritorna HTML per l'editing delle associazioni Ruoli->Permessi.
   * @return html
   * @throws java.lang.Exception
   */
  public String getHtmlTedit6SelectedPermissions()
     throws Exception
  {
    StringBuilder sOut = new StringBuilder(8192);
    sOut.append(
       "<div class='rigel_formtable'>"
       + "<" + tagTabelleForm + ">\r\n"
       + "<" + sRowHeader + ">"
       + "<" + sColHeader + " colspan=2 rowspan=2 align=center>" + locRuoli + "</td>"
       + "<" + sColHeader + " colspan=" + (pidSelected.length) + " align=center>" + locPermessi + "</td>"
       + "</tr>\r\n");

    String baseUrlRPerms = LI.getLinkUrl(PAGE_ONEROLE_PERMISSION) + "?roleid=";

    sOut.append("<tr>");
    for(int i = 0; i < pidSelected.length; i++)
    {
      int idPerm = pidSelected[i];
      TurbinePermission tp = getPermission(idPerm);
      sOut.append("<td>" + tp.getName() + "</td>");
    }
    sOut.append("</tr>");

    for(int r = 0; r < vRole.size(); r++)
    {
      TurbineRole tr = (TurbineRole) (vRole.get(r));
      int roleId = tr.getEntityId();

      sOut.append("<tr>");
      sOut.append("<td align=left><a href=\"" + baseUrlRPerms + roleId + "\">" + tr.getName() + "</a></td>");
      sOut.append("<td align=right>" + drawButSI("tuttiRoleSI(" + roleId + ")"));
      sOut.append(drawButNO("tuttiRoleNO(" + roleId + ")") + "</td>");

      for(int i = 0; i < pidSelected.length; i++)
      {
        int idPerm = pidSelected[i];
        TurbinePermission p = getPermission(idPerm);
        String cbName = "RPC_" + idPerm + "_" + roleId;
        String hidName = "HRPC_" + idPerm + "_" + roleId;

        sOut.append(
           "<td><input type=\"checkbox\" name=\"" + cbName + "\" value=\"1\" "
           + (checkRolePerm(roleId, idPerm) ? "checked" : "") + ">"
           + "<input type=\"hidden\" name=\"" + hidName + "\" value=\"1\" ></td>\n");
      }

      sOut.append("</tr>");
    }

    sOut.append("<tr><td colspan=2>&nbsp;</td>");
    for(int i = 0; i < pidSelected.length; i++)
    {
      int idPerm = pidSelected[i];
      sOut.append("<td align=left>" + drawButSI("tuttiSI(" + idPerm + ")"));
      sOut.append(drawButNO("tuttiNO(" + idPerm + ")") + "</td>");
    }
    sOut.append("</tr>");

    sOut.append("</table></div>\r\n");
    return sOut.toString();
  }

  /**
   * Ritorna HTML per l'editing delle associazioni Ruoli->Permessi.
   * @return html
   * @throws java.lang.Exception
   */
  public String getHtmlTedit6SelectedRoles()
     throws Exception
  {
    StringBuilder sOut = new StringBuilder(8192);
    sOut.append(
       "<div class='rigel_formtable'>"
       + "<" + tagTabelleForm + ">\r\n"
       + "<" + sRowHeader + ">"
       + "<" + sColHeader + " colspan=2 rowspan=2 align=center>" + locPermessi + "</td>"
       + "<" + sColHeader + " colspan=" + (tidSelected.length) + " align=center>" + locRuoli + "</td>"
       + "</tr>\r\n");

    String baseUrlPRuoli = LI.getLinkUrl(PAGE_ONEPERMISSION_RUOLI) + "?permid=";

    sOut.append("<tr>");
    for(int i = 0; i < tidSelected.length; i++)
    {
      int idRole = tidSelected[i];
      TurbineRole r = getRole(idRole);
      sOut.append("<td>" + r.getName() + "</td>");
    }
    sOut.append("</tr>");

    for(int p = 0; p < vPerm.size(); p++)
    {
      TurbinePermission tp = (TurbinePermission) (vPerm.get(p));
      int permId = tp.getEntityId();

      sOut.append("<tr>");
      sOut.append("<td align=left><a href=\"" + baseUrlPRuoli + tp.getEntityId() + "\">" + tp.getName() + "</a></td>");
      sOut.append("<td align=right>" + drawButSI("tuttiSI(" + permId + ")"));
      sOut.append(drawButNO("tuttiNO(" + permId + ")") + "</td>");

      for(int i = 0; i < tidSelected.length; i++)
      {
        int idRole = tidSelected[i];
        String cbName = "RPC_" + permId + "_" + idRole;
        String hidName = "HRPC_" + permId + "_" + idRole;

        sOut.append(
           "<td><input type=\"checkbox\" name=\"" + cbName + "\" value=\"1\" "
           + (checkRolePerm(idRole, permId) ? "checked" : "") + ">"
           + "<input type=\"hidden\" name=\"" + hidName + "\" value=\"1\" ></td>\n");
      }

      sOut.append("</tr>");
    }

    sOut.append("<tr><td colspan=2>&nbsp;</td>");
    for(int r = 0; r < tidSelected.length; r++)
    {
      int idRole = tidSelected[r];
      sOut.append("<td align=left>" + drawButSI("tuttiRoleSI(" + idRole + ")"));
      sOut.append(drawButNO("tuttiRoleNO(" + idRole + ")") + "</td>");
    }
    sOut.append("</tr>");

    sOut.append("</table></div>\r\n");
    return sOut.toString();
  }

  public String getHtmlComboGruppi()
  {
    String rv = "";

    for(Iterator it = vGroup.iterator(); it.hasNext();)
    {
      TurbineGroup g = (TurbineGroup) it.next();
      String sel = groupId == g.getEntityId() ? "selected" : "";
      rv += "<option value=\"" + g.getEntityId() + "\" " + sel + ">" + g.getName() + "</option>";
    }

    return rv;
  }

  public void storeUserGroupRole(RunData data)
     throws Exception
  {
    // recupera i gruppi diversi da quello corrente
    Vector vTemp = new Vector(10, 10);
    Iterator iter = vUserGroupRole.iterator();
    while(iter.hasNext())
    {
      TurbineUserGroupRole ugr = (TurbineUserGroupRole) (iter.next());

      // controlla se il gruppo e' quello interessato;
      // diversamente li aggiunge al set in uscita
      if(ugr.getGroupId() != groupId)
      {
        vTemp.add(ugr);
        continue;
      }

      // controlla che questo TurbineUserGroupRole sia stato sottoposto
      // ad editing; diversamente lo aggiunge al set in uscita
      String hidName = "HUGR_" + ugr.getUserId() + "_" + ugr.getRoleId();
      if(data.getParameters().getString(hidName) == null)
        vTemp.add(ugr);
    }

    // costruisce dati di quello corrente
    int num = 0;
    for(int u = 0; u < vUser.size(); u++)
    {
      TurbineUser us = (TurbineUser) (vUser.get(u));
      int userId = us.getEntityId();

      for(int r = 0; r < vRole.size(); r++)
      {
        TurbineRole tr = (TurbineRole) (vRole.get(r));
        int roleId = tr.getEntityId();
        String cbName = "RUGR_" + userId + "_" + roleId;

        if(data.getParameters().getString(cbName) != null)
        {
          TurbineUserGroupRole ugr = new TurbineUserGroupRole();
          ugr.setUserId(userId);
          ugr.setGroupId(groupId);
          ugr.setRoleId(roleId);
          vTemp.add(ugr);
          num++;
        }
      }
    }

    // dati persi?
    if(num == 0)
    {
      vUserGroupRole = TurbineUserGroupRolePeer.doSelect(new Criteria());
      return;
    }

    vUserGroupRole = vTemp;
  }

  public void storeRolePermission(RunData data)
     throws Exception
  {
    // recupera le entry non sottoposte a editing
    Vector vTemp = new Vector();
    Iterator iter = vRolePerm.iterator();
    while(iter.hasNext())
    {
      TurbineRolePermission trp = (TurbineRolePermission) (iter.next());

      // controlla che questo TurbineRolePermission sia stato sottoposto
      // ad editing; diversamente lo aggiunge al set in uscita
      String hidName = "HRPC_" + trp.getPermissionId() + "_" + trp.getRoleId();
      if(data.getParameters().getString(hidName) == null)
        vTemp.add(trp);
    }

    int num = 0;
    for(int p = 0; p < vPerm.size(); p++)
    {
      TurbinePermission tp = (TurbinePermission) (vPerm.get(p));
      int permId = tp.getEntityId();

      for(int r = 0; r < vRole.size(); r++)
      {
        TurbineRole tr = (TurbineRole) (vRole.get(r));
        int roleId = tr.getEntityId();
        String cbName = "RPC_" + permId + "_" + roleId;

        if(data.getParameters().getString(cbName) != null)
        {
          TurbineRolePermission trp = new TurbineRolePermission();
          trp.setRoleId(roleId);
          trp.setPermissionId(permId);
          vTemp.add(trp);
          num++;
        }
      }
    }

    // dati persi?
    if(num == 0)
    {
      vRolePerm = TurbineRolePermissionPeer.doSelect(new Criteria());
      return;
    }

    vRolePerm = vTemp;
  }
  private static final Object semaforoSalva = new Object();

  public void saveUserGroupRole()
     throws Exception
  {
    synchronized(semaforoSalva)
    {
      Connection con = null;
      try
      {
        con = Transaction.begin();
        saveUserGroupRole(con);
        Transaction.commit(con);
      }
      catch(Exception e)
      {
        Transaction.safeRollback(con);
        throw e;
      }
    }
  }

  public void saveUserGroupRole(Connection dbCon)
     throws Exception
  {
    // salvataggio associazione utenti-gruppi-ruoli
    DbUtils.executeStatement("DELETE FROM " + TurbineUserGroupRolePeer.TABLE_NAME, dbCon);

    try (PreparedStatement ps = dbCon.prepareStatement(
       "INSERT INTO turbine_user_group_role(\n"
       + "	user_id, group_id, role_id)\n"
       + "	VALUES (?, ?, ?)"))
    {
      Iterator iterUgr = vUserGroupRole.iterator();
      while(iterUgr.hasNext())
      {
        TurbineUserGroupRole ugr = (TurbineUserGroupRole) (iterUgr.next());

        ps.setInt(1, ugr.getUserId());
        ps.setInt(2, ugr.getGroupId());
        ps.setInt(3, ugr.getRoleId());
        ps.executeUpdate();
      }
    }
  }

  public void saveRolePermission()
     throws Exception
  {
    synchronized(semaforoSalva)
    {
      Connection con = null;
      try
      {
        con = Transaction.begin();
        saveRolePermission(con);
        Transaction.commit(con);
      }
      catch(Exception e)
      {
        Transaction.safeRollback(con);
        throw e;
      }
    }
  }

  public void saveRolePermission(Connection dbCon)
     throws Exception
  {
    // salvataggio associazione ruoli-permessi
    DbUtils.executeStatement("DELETE FROM " + TurbineRolePermissionPeer.TABLE_NAME, dbCon);

    try (PreparedStatement ps = dbCon.prepareStatement(
       "INSERT INTO turbine_role_permission(\n"
       + "	role_id, permission_id)\n"
       + "	VALUES (?, ?)"))
    {
      Iterator iterRpe = vRolePerm.iterator();
      while(iterRpe.hasNext())
      {
        TurbineRolePermission tpr = (TurbineRolePermission) (iterRpe.next());

        ps.setInt(1, tpr.getRoleId());
        ps.setInt(2, tpr.getPermissionId());
        ps.executeUpdate();
      }
    }
  }

  /**
   * Esegue import da file xml.
   * @param name nome del file
   * @param inputStream stream di ingresso
   * @param ckremove flag per cancellazione preventiva ruoli e permessi
   * @throws Exception
   */
  public void importDaFileXML(String name, InputStream inputStream, boolean ckremove)
     throws Exception
  {
    synchronized(semaforoSalva)
    {
      Connection con = null;
      try
      {
        con = Transaction.begin();
        importDaFileXML(con, name, inputStream, ckremove);
        Transaction.commit(con);

        // legge il nuovo set dal db
        leggiDati();
      }
      catch(Exception e)
      {
        Transaction.safeRollback(con);
        throw e;
      }
    }
  }

  public void importDaFileXML(Connection dbCon, String name, InputStream inputStream, boolean ckremove)
     throws Exception
  {
    SAXBuilder builder = new SAXBuilder();
    Document doc = builder.build(inputStream);
    Element root = doc.getRootElement();
    if(root == null)
      throw new ErrorMessageException(i18n.msg("File XML non valido: nessun root document."));

    Element roles = root.getChild("roles");
    Element permissions = root.getChild("permissions");
    Element grants = root.getChild("grants");

    if(roles == null || permissions == null || grants == null)
      throw new ErrorMessageException(i18n.msg("File XML non valido: sezioni roles, permissions, grants sono obbligatorie."));

    // mappa degli utenti, gruppi e ruoli
    HashSet<String> userGroupRoleSet = new HashSet<String>();

    if(ckremove)
    {
      // salva utenti gruppi ruoli per ripristino successivo
      for(Iterator it = vUserGroupRole.iterator(); it.hasNext();)
      {
        TurbineUserGroupRole ugr = (TurbineUserGroupRole) it.next();
        TurbineUser user = getUser(ugr.getUserId());
        TurbineGroup group = getGroup(ugr.getGroupId());
        TurbineRole role = getRole(ugr.getRoleId());
        if(user != null && group != null && role != null)
          userGroupRoleSet.add(user.getEntityName() + "|" + group.getName() + "|" + role.getName());
      }

      // rimuove ruoli e permessi esistenti
      DbUtils.executeStatement("DELETE FROM " + TurbineUserGroupRolePeer.TABLE_NAME, dbCon);
      DbUtils.executeStatement("DELETE FROM " + TurbineRolePermissionPeer.TABLE_NAME, dbCon);
      DbUtils.executeStatement("DELETE FROM " + TurbineRolePeer.TABLE_NAME, dbCon);
      DbUtils.executeStatement("DELETE FROM " + TurbinePermissionPeer.TABLE_NAME, dbCon);

      vRole.clear();
      vPerm.clear();
      vRolePerm.clear();
      vUserGroupRole.clear();
    }

    // carica tutti i ruoli
    List lsRole = roles.getChildren("role");
    for(Iterator itRole = lsRole.iterator(); itRole.hasNext();)
    {
      try
      {
        Element eRole = (Element) itRole.next();
        String roleName = eRole.getAttributeValue("name");
        if(!roleName.equals("0") && findRole(roleName) == null)
        {
          TurbineRole tr = new TurbineRole();
          tr.setName(roleName);
          tr.save(dbCon);
          vRole.add(tr);
        }
      }
      catch(Exception e1)
      {
        e1.printStackTrace();
      }
    }

    // carica tutti i permessi
    List lsPermission = permissions.getChildren("permission");
    for(Iterator itPermission = lsPermission.iterator(); itPermission.hasNext();)
    {
      try
      {
        Element ePermission = (Element) itPermission.next();
        String permissionName = ePermission.getAttributeValue("name");
        if(!permissionName.equals("0") && findPermission(permissionName) == null)
        {
          TurbinePermission tp = new TurbinePermission();
          tp.setName(permissionName);
          tp.save(dbCon);
          vPerm.add(tp);
        }
      }
      catch(Exception e1)
      {
        e1.printStackTrace();
      }
    }

    // associa ruoli e permessi
    vRolePerm.clear();
    List lsGrant = grants.getChildren("grant");
    for(Iterator itGrant = lsGrant.iterator(); itGrant.hasNext();)
    {
      Element eGrant = (Element) itGrant.next();
      String roleName = eGrant.getAttributeValue("role");
      String permName = eGrant.getAttributeValue("permission");
      TurbineRole tr = findRole(roleName);
      TurbinePermission tp = findPermission(permName);
      if(tr != null && tp != null)
      {
        TurbineRolePermission trp = new TurbineRolePermission();
        trp.setTurbineRole(tr);
        trp.setTurbinePermission(tp);
        vRolePerm.add(trp);
      }
    }

    // salva dati su db
    saveRolePermission(dbCon);

    if(ckremove)
    {
      // associa utenti gruppi ruoli
      vUserGroupRole.clear();
      for(Iterator<String> it = userGroupRoleSet.iterator(); it.hasNext();)
      {
        String key = it.next();

        String[] ss = SU.split(key, '|');
        TurbineUser user = findUser(ss[0]);
        TurbineGroup group = findGroup(ss[1]);
        TurbineRole role = findRole(ss[2]);

        if(user != null && group != null && role != null)
        {
          TurbineUserGroupRole ugr = new TurbineUserGroupRole();
          ugr.setTurbineUser(user);
          ugr.setTurbineGroup(group);
          ugr.setTurbineRole(role);
          vUserGroupRole.add(ugr);
        }
      }

      // salva dati su db
      saveUserGroupRole(dbCon);
    }
  }
}
