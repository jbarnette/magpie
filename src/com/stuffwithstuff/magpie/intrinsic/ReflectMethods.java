package com.stuffwithstuff.magpie.intrinsic;

import com.stuffwithstuff.magpie.Def;
import com.stuffwithstuff.magpie.ast.pattern.Pattern;
import com.stuffwithstuff.magpie.ast.pattern.RecordPattern;
import com.stuffwithstuff.magpie.interpreter.Callable;
import com.stuffwithstuff.magpie.interpreter.ClassObj;
import com.stuffwithstuff.magpie.interpreter.Context;
import com.stuffwithstuff.magpie.interpreter.Multimethod;
import com.stuffwithstuff.magpie.interpreter.Name;
import com.stuffwithstuff.magpie.interpreter.Obj;

public class ReflectMethods {
  @Def("(_) class")
  public static class Class_ implements Intrinsic {
    public Obj invoke(Context context, Obj arg) {
      return arg.getField(0).getClassObj();
    }
  }

  @Def("(_) is?(class is Class)")
  public static class Is implements Intrinsic {
    public Obj invoke(Context context, Obj arg) {
      return context.toObj(arg.getField(0).getClassObj().isSubclassOf(
          arg.getField(1).asClass()));
    }
  }
  
  @Def("(_) sameAs?(other)")
  public static class Same implements Intrinsic {
    public Obj invoke(Context context, Obj arg) {
      return context.toObj(
          arg.getField(0) == arg.getField(1));
    }
  }
  
  @Def("docMethod(methodName is String)")
  public static class DocMethod implements Intrinsic {
    public Obj invoke(Context context, Obj arg) {
      String name = arg.getField(1).asString();
      
      // TODO(bob): Hackish, but works.
      Multimethod multimethod = context.getModule().getScope()
          .lookUpMultimethod(name);
      
      if (multimethod == null) {
        return context.toObj(
            "Couldn't find a method named \"" + name + "\".\n");
      } else {
        StringBuilder builder = new StringBuilder();
        
        for (Callable method : multimethod.getMethods()) {
          Pattern pattern = method.getPattern();
          if (pattern instanceof RecordPattern) {
            RecordPattern record = (RecordPattern)pattern;
            builder.append(String.format("(%s) %s(%s)\n",
                record.getFields().get(Name.getTupleField(0)),
                name,
                record.getFields().get(Name.getTupleField(1))));
          } else {
            builder.append(String.format("%s %s\n",
                pattern, name));
          }
          
          builder.append("| " + method.getDoc().replace("\n", "\n| ") + "\n");
        }
        
        return context.toObj(builder.toString());
      }
    }
  }
  
  @Def("(is Class) doc")
  public static class ClassDoc implements Intrinsic {
    public Obj invoke(Context context, Obj arg) {
      ClassObj classObj = arg.getField(0).asClass();
      
      StringBuilder builder = new StringBuilder();
      builder.append(classObj.getName() + "\n");
      if (classObj.getDoc().length() > 0) {
        builder.append("| " + classObj.getDoc().replace("\n", "\n| ") + "\n");
      } else {
        builder.append("| <no doc>\n");
      }
      return context.toObj(builder.toString());
    }
  }
}
