// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.prim.etc;

import org.nlogo.core.AgentKindJ;
import org.nlogo.agent.AgentSet;
import org.nlogo.agent.Link;
import org.nlogo.api.Dump;
import org.nlogo.core.I18N;
import org.nlogo.api.LogoException;
import org.nlogo.core.LogoList;
import org.nlogo.api.Syntax;
import org.nlogo.nvm.ArgumentTypeException;
import org.nlogo.nvm.Context;
import org.nlogo.nvm.EngineException;
import org.nlogo.nvm.Reporter;

import java.util.LinkedHashSet;
import java.util.Set;

public final strictfp class _linkset
    extends Reporter {
  @Override
  public org.nlogo.core.Syntax syntax() {
    int[] right = {Syntax.RepeatableType() | Syntax.LinkType()
        | Syntax.LinksetType() | Syntax.NobodyType()
        | Syntax.ListType()};
    int ret = Syntax.LinksetType();
    return Syntax.reporterSyntax(right, ret, 1, 0);
  }

  @Override
  public Object report(final Context context)
      throws LogoException {
    LinkedHashSet<Link> resultSet = new LinkedHashSet<Link>();
    for (int i = 0; i < args.length; i++) {
      Object elt = args[i].report(context);
      if (elt instanceof AgentSet) {
        AgentSet tempSet = (AgentSet) elt;
        if (tempSet.type() != org.nlogo.agent.Link.class) {
          throw new ArgumentTypeException
              (context, this, i, Syntax.LinkType() | Syntax.LinksetType(), elt);
        }
        for (AgentSet.Iterator iter = tempSet.iterator(); iter.hasNext();) {
          resultSet.add((Link) iter.next());
        }
      } else if (elt instanceof LogoList) {
        descendList(context, (LogoList) elt, resultSet);
      } else if (elt instanceof Link) {
        resultSet.add((Link) elt);
      } else if (elt != org.nlogo.core.Nobody$.MODULE$) {
        throw new ArgumentTypeException
            (context, this, i, Syntax.LinkType() | Syntax.LinksetType(), elt);
      }
    }
    return new org.nlogo.agent.ArrayAgentSet(
        AgentKindJ.Link(),
        resultSet.toArray(new org.nlogo.agent.Link[resultSet.size()]));
  }

  private void descendList(Context context, LogoList tempList, Set<Link> result)
      throws LogoException {
    for (Object obj : tempList.javaIterable()) {
      if (obj instanceof Link) {
        result.add((Link) obj);
      } else if (obj instanceof AgentSet) {
        AgentSet tempSet = (AgentSet) obj;
        if (tempSet.type() != org.nlogo.agent.Link.class) {
          throw new EngineException(context, this,
              I18N.errorsJ().getN("org.nlogo.prim.etc._linkset.invalidLAgentsetTypeInputToList",
                  this.displayName(), Dump.logoObject(tempList, true, false), Dump.logoObject(obj, true, false)));
        }
        for (AgentSet.Iterator iter2 = tempSet.iterator();
             iter2.hasNext();) {
          result.add((Link) iter2.next());
        }
      } else if (obj instanceof LogoList) {
        descendList(context, (LogoList) obj, result);
      } else if (obj != org.nlogo.core.Nobody$.MODULE$) {
        throw new EngineException(context, this,
            I18N.errorsJ().getN("org.nlogo.prim.etc._linkset.invalidListInputs",
                this.displayName(), Dump.logoObject(tempList, true, false), Dump.logoObject(obj, true, false)));
      }
    }
  }
}