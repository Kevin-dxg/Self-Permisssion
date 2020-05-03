package com.github.kevin.complier;

import com.github.kevin.annotation.NeedsPermisssion;
import com.github.kevin.annotation.OnNeverAskAgain;
import com.github.kevin.annotation.OnPermisssionDenied;
import com.github.kevin.annotation.OnShowRationable;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

//@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Process.class) //自动解析注解为文件
public class PermissionProcessor1 extends AbstractProcessor {
//    private Messager messager;//用来报告错误、警告、提示
//    private Elements elementsUtils;//包含了很多操作Elements的工具方法
//    private Filer filer;//用来创建新的源文件.class文件（APT - 造币技术）
//    private Types typesUtils;//包含用于操作TypeMirror的工具方法
//
//    //初始化工作
//    @Override
//    public synchronized void init(ProcessingEnvironment processingEnvironment) {
//        super.init(processingEnvironment);
//        messager = processingEnvironment.getMessager();
//        elementsUtils = processingEnvironment.getElementUtils();
//        filer = processingEnvironment.getFiler();
//        typesUtils = processingEnvironment.getTypeUtils();
//    }

    //获取支持注解的类型
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(NeedsPermisssion.class.getCanonicalName());
        types.add(OnNeverAskAgain.class.getCanonicalName());
        types.add(OnPermisssionDenied.class.getCanonicalName());
        types.add(OnShowRationable.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        //返回注解支持的最新原版本 JDK
        //@SupportedSourceVersion(SourceVersion.RELEASE_8) 支持注解表达
        return SourceVersion.latest();
    }

    //注解处理器的核心方法，处理具体的注解实现，生成java文件
    //强调的是一行代码一行代码的去写
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<String, List<ExecutableElement>> needsPermissionMap = getPermissionMap(roundEnvironment, NeedsPermisssion.class);
        Map<String, List<ExecutableElement>> onShowRationableMap = getPermissionMap(roundEnvironment, OnShowRationable.class);
        Map<String, List<ExecutableElement>> onPermisssionDeniedMap = getPermissionMap(roundEnvironment, OnPermisssionDenied.class);
        Map<String, List<ExecutableElement>> onNeverAskAgainMap = getPermissionMap(roundEnvironment, OnNeverAskAgain.class);
        //开始造币
        for (String activityName : needsPermissionMap.keySet()) {
            List<ExecutableElement> needsPermissionElements = needsPermissionMap.get(activityName);
            List<ExecutableElement> onShowRationableElements = onShowRationableMap.get(activityName);
            List<ExecutableElement> onPermisssionDeniedElements = onPermisssionDeniedMap.get(activityName);
            List<ExecutableElement> onNeverAskAgainElements = onNeverAskAgainMap.get(activityName);
            final String CLASS_SUFFIX = "$Permissions";
            Filer filer = processingEnv.getFiler();
            try {
                //创建一个class源文件，并返回一个对象以允许写入它(MainActivity$Permission)
                JavaFileObject javaFileObject = filer.createClassFile(activityName + CLASS_SUFFIX);
                String packageName = getPackageName(needsPermissionElements.get(0));
                //定义一个Writer对象，开启造币过程
                Writer writer = javaFileObject.openWriter();
                String activitySimpleName = needsPermissionElements.get(0).getEnclosingElement().getSimpleName().toString() + CLASS_SUFFIX;
                //生成包
                writer.write("package" + packageName + ";\n");
                //生成要导入的接口类（必须手动导入）
                writer.write("import com.github.kevin.library.listener.PermissionRequest;\n");
                writer.write("import com.github.kevin.library.listener.RequestPermission;\n");
                writer.write("import com.github.kevin.library.utils.PermissionUtils;\n");
                writer.write("import android.support.v7.app.AppCompatActivity;\n");
                writer.write("import android.support.v7.app.ActivityCompat;\n");
                writer.write("import android.support.annotation.NonNull;\n");
                writer.write("import java.lang.ref.WeakReference;\n");
                //生成类
                writer.write("public class" + activityName + "implements RequestPermission<" + activityName + "> { \n }");
                //生成常量运算符
                writer.write("private static final int REQUEST_SHOWCAMERA = 666;\n");
                writer.write("private static String[] PERMISSION_SHOWCAMERA;\n");
                //生成requestPermission方法
                writer.write("public void requestPermission(" + activityName + " target, String[] permissions) {\n");
                writer.write("PERMISSION_SHOWCAMERA = permissions;\n");
                writer.write("if (PermissionUtils.hasSelfPermission(target, PERMISSION_SHOWCAMERA)) {\n");
                //循环生成MainActivity每个权限申请方法
                if (needsPermissionElements != null && !needsPermissionElements.isEmpty()) {
                    for (ExecutableElement executableElement : needsPermissionElements) {
                        String methodName = executableElement.getSimpleName().toString();
                        writer.write("target." + methodName + "();\n");
                    }
                }
                writer.write("} else if (PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_SHOWCAMERA)) {\n");
                //循环生成MainActivity每个不再询问后的提示
//                if (onShowRationableElements != null && !onShowRationableElements.isEmpty()) {
//                    for (ExecutableElement executableElement : onShowRationableElements) {
//                        String methodName = executableElement.getSimpleName().toString();
//                        writer.write("target." + methodName + "();\n");
//                    }
//                }
                writer.write("} else {\n");
                writer.write("ActivityCompat.requestPermissions(target, PERMISSION_SHOWCAMERA, REQUEST_SHOWCAMERA);\n}\n}\n");
                //生成onRequestPermissionResult方法
                writer.write(" public void onRequestPermissionResult(MainActivity target, int requestCode, @NonNull int[] grantResults) {\n");
                writer.write("switch (requestCode) {\n");
                writer.write("case REQUEST_SHOWCAMERA:\n");
                writer.write("if (PermissionUtils.verifyPermission(grantResults)) {\n");
                //循环生成MainActivity每个权限申请方法
                if (needsPermissionElements != null && !needsPermissionElements.isEmpty()) {
                    for (ExecutableElement executableElement : needsPermissionElements) {
                        String methodName = executableElement.getSimpleName().toString();
                        writer.write("target." + methodName + "();\n");
                    }
                }
                writer.write("} else if (!PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_SHOWCAMERA)) {\n");
                //循环生成MainActivity选择不再询问后的提示方法
                if (onNeverAskAgainElements != null && !onNeverAskAgainElements.isEmpty()) {
                    for (ExecutableElement executableElement : onNeverAskAgainElements) {
                        String methodName = executableElement.getSimpleName().toString();
                        writer.write("target." + methodName + "();\n");
                    }
                }
                writer.write("} else {\n");
                //循环生成MainActivity每个拒绝时的提示方法
                if (onPermisssionDeniedElements != null && !onPermisssionDeniedElements.isEmpty()) {
                    for (ExecutableElement executableElement : onPermisssionDeniedElements) {
                        String methodName = executableElement.getSimpleName().toString();
                        writer.write("target." + methodName + "();\n");
                    }
                }
                writer.write("}\ndefault:\nbreak;\n}\n}\n");
                //生成静态PermissionRequestImpl内部类
                writer.write("private static final class PermissionRequestImpl implements PermissionRequest {\n");
                writer.write("private final WeakReference<MainActivity> weakTarget;\n");
                writer.write("private PermissionRequestImpl(MainActivity target) {\n");
                writer.write("this.weakTarget = new WeakReference<>(target);\n}\n");
                writer.write("public void proceed() {\n");
                writer.write("MainActivity target = this.weakTarget.get();\n");
                writer.write("if (target != null) {\n");
                writer.write("ActivityCompat.requestPermissions(target, MainActivity$Permissions.PERMISSION_SHOWCAMERA, REQUEST_SHOWCAMERA);\n}\n}\n}\n}\n");
               //最后结束标签，造币完成
                writer.write("\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private Map<String, List<ExecutableElement>> getPermissionMap(RoundEnvironment roundEnvironment, Class<? extends Annotation> clazz) {
        //获取MainActivity中所有带NeedsPermisssion注解的方法
        Set<? extends Element> needsPermissionSet = roundEnvironment.getElementsAnnotatedWith(clazz);
        //保存起来，键值对：key-com.xxx.MainActivity，value-所有带NeedsPermisssion注解的方法
        Map<String, List<ExecutableElement>> needsPermissionMap = new HashMap<>();
        //遍历所有带NeedsPermisssion注解的方法
        for (Element element : needsPermissionSet) {
            //转成方法元素（结构体元素）
            ExecutableElement executableElement = (ExecutableElement) element;
            String activityName = getActivityName(executableElement);
            //从缓存几个钟获取MainActivity所有带NeedsPermisssion注解的方法集合
            List<ExecutableElement> list = needsPermissionMap.get(activityName);
            if (list == null) {
                list = new ArrayList<>();
                //先加入map集合，引用变量list可以动态改变值
                needsPermissionMap.put(activityName, list);
            }
            //将MainActivity所有带NeedsPermisssion注解的方法加入到list集合
            list.add(executableElement);
        }
        return needsPermissionMap;
    }


    private String getActivityName(ExecutableElement executableElement) {
        //通过方法标签获取类名标签，再通过类名标签获取包名标签
        String packageName = getPackageName(executableElement);
        //通过方法标签获取类名标签
        TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
        //完整字符串拼接：com.xxx.xxx + "." + MainActivity
        return packageName + "." + typeElement.getSimpleName().toString();
    }

    private String getPackageName(ExecutableElement executableElement) {
        //通过方法标签获取类名标签
        TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
        //通过类名标签获取包名标签
        String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        System.out.println("packageName >> " + packageName);
        return packageName;
    }

}
