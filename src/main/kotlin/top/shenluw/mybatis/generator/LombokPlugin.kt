package top.shenluw.mybatis.generator

import org.mybatis.generator.api.IntrospectedColumn
import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.Plugin.ModelClassType
import org.mybatis.generator.api.PluginAdapter
import org.mybatis.generator.api.dom.java.Method
import org.mybatis.generator.api.dom.java.TopLevelClass
import org.mybatis.generator.internal.util.StringUtility
import java.util.*
import java.util.regex.Pattern


/**
 *  参考 https://github.com/itfsw/mybatis-generator-plugin
 *
 * @author shenluw
 * @date 2021/4/28 10:01
 */
class LombokPlugin : PluginAdapter() {

    private val LOMBOK_ANNOTATION = Pattern.compile("@([a-zA-z]+)(\\(.*\\))?")

    private val LOMBOK_FEATURES = listOf(
        "Getter", "Setter", "ToString", "EqualsAndHashCode", "NoArgsConstructor",
        "RequiredArgsConstructor", "AllArgsConstructor", "Data", "Value", "Builder", "Log"
    )
    private val LOMBOK_EXPERIMENTAL_FEATURES = listOf(
        "Accessors",
        "FieldDefaults",
        "Wither",
        "UtilityClass",
        "Helper",
        "FieldNameConstants",
        "SuperBuilder"
    )

    private var annotations: MutableList<String>? = null

    override fun validate(warnings: MutableList<String?>): Boolean {
        val properties = getProperties()

        for (key in properties.stringPropertyNames()) {
            val annotation = key.trim()
            if (!(annotation.matches(LOMBOK_ANNOTATION.toRegex()))) {
                warnings.add("mybatis-generator-lombok: not support（$annotation）")
                return false
            }
        }
        return true
    }

    override fun initialized(introspectedTable: IntrospectedTable) {
        annotations = mutableListOf()
        val properties: Properties = getProperties()
        var findData = false
        for (key in properties.stringPropertyNames()) {
            val annotation = key.trim()
            if (annotation.startsWith("@Data")) {
                findData = true
            }
            if (StringUtility.isTrue(properties.getProperty(key))) {
                annotations?.add(annotation)
            }
        }
        if (!findData) {
            annotations?.add(0, "@Data")
        }
    }

    override fun modelBaseRecordClassGenerated(
        topLevelClass: TopLevelClass,
        introspectedTable: IntrospectedTable
    ): Boolean {
        addAnnotations(topLevelClass, introspectedTable)
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable)
    }

    override fun modelPrimaryKeyClassGenerated(
        topLevelClass: TopLevelClass,
        introspectedTable: IntrospectedTable
    ): Boolean {
        addAnnotations(topLevelClass, introspectedTable)
        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable)
    }

    override fun modelRecordWithBLOBsClassGenerated(
        topLevelClass: TopLevelClass,
        introspectedTable: IntrospectedTable
    ): Boolean {
        addAnnotations(topLevelClass, introspectedTable)
        return super.modelRecordWithBLOBsClassGenerated(topLevelClass, introspectedTable)
    }

    override fun modelGetterMethodGenerated(
        method: Method?,
        topLevelClass: TopLevelClass?,
        introspectedColumn: IntrospectedColumn?,
        introspectedTable: IntrospectedTable?,
        modelClassType: ModelClassType?
    ): Boolean {
        for (annotation in annotations!!) {
            if (annotation.startsWith("@Data") || annotation.startsWith("@Getter")) {
                return false
            }
        }
        return true
    }

    override fun modelSetterMethodGenerated(
        method: Method?,
        topLevelClass: TopLevelClass?,
        introspectedColumn: IntrospectedColumn?,
        introspectedTable: IntrospectedTable?,
        modelClassType: ModelClassType?
    ): Boolean {
        for (annotation in annotations!!) {
            if (annotation.startsWith("@Data") || annotation.startsWith("@Setter")) {
                return false
            }
        }
        return true
    }

    private fun addAnnotations(topLevelClass: TopLevelClass, introspectedTable: IntrospectedTable) {
        for (annotation in annotations!!) {
            // @Data
            if (annotation.startsWith("@Data")) {
                addAnnotation(topLevelClass, annotation)
                if (topLevelClass.superClass != null) {
                    addAnnotation(topLevelClass, "@EqualsAndHashCode(callSuper = true)")
                    addAnnotation(topLevelClass, "@ToString(callSuper = true)")
                }
            } else {
                addAnnotation(topLevelClass, annotation)
            }
        }
    }

    /**
     * 添加注解
     * @param topLevelClass
     * @param annotation
     */
    private fun addAnnotation(topLevelClass: TopLevelClass, annotation: String) {
        // 正则提取annotation
        val matcher = LOMBOK_ANNOTATION.matcher(annotation)
        if (matcher.find()) {
            val annotationName: String = matcher.group(1)
            if (LOMBOK_FEATURES.contains(annotationName)) {
                topLevelClass.addImportedType("lombok.$annotationName")
            } else if (LOMBOK_EXPERIMENTAL_FEATURES.contains(annotationName)) {
                topLevelClass.addImportedType("lombok.experimental.$annotationName")
            } else {
                return
            }
            topLevelClass.addAnnotation(annotation)
        }
    }
}