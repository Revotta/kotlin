package org.jetbrains.kotlin.tools

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import org.w3c.dom.*

/**
* This tool generates JavaScript stubs for classes available in the JDK which are already available in the browser environment
* such as the W3C DOM
*/
fun generateDomAPI(file: File): Unit {
    write(file) {

        println("""
package org.w3c.dom

//
// NOTE THIS FILE IS AUTO-GENERATED by the GeneratedJavaScriptStubs.kt
// See: https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib
//

import js.noImpl

// Contains stub APIs for the W3C DOM API so we can delegate to the platform DOM instead

""")

        val classes = arrayList(javaClass<Attr>(), javaClass<CDATASection>(),
                javaClass<CharacterData>(), javaClass<Comment>(),
                javaClass<Document>(),javaClass<DocumentFragment>(),javaClass<DocumentType>(),
                javaClass<DOMConfiguration>(),
                javaClass<DOMError>(), javaClass<DOMErrorHandler>(),
                javaClass<DOMImplementation>(),
                javaClass<DOMLocator>(),
                javaClass<DOMStringList>(),
                javaClass<Element>(),
                javaClass<Entity>(), javaClass<EntityReference>(),
                javaClass<NameList>(), javaClass<NamedNodeMap>(), javaClass<Node>(), javaClass<NodeList>(),
                javaClass<Notation>(), javaClass<ProcessingInstruction>(),
                javaClass<Text>(), javaClass<TypeInfo>(),
                javaClass<UserDataHandler>())

        fun simpleTypeName(klass: Class<out Any?>?): String {
            val answer = klass?.getSimpleName()?.capitalize() ?: "Unit"
            return if (answer == "Void") "Unit" else if (answer == "Object") "Any" else answer
        }

        for (klass in classes) {
            val interfaces = klass.getInterfaces()
            val extends = if (interfaces != null && interfaces.size == 1) ": ${interfaces[0]?.getSimpleName()}" else ""

            println("native public trait ${klass.getSimpleName()}$extends {")

            // lets iterate through each method
            val methods = klass.getDeclaredMethods()
            if (methods != null) {
                for (method in methods) {
                    if (method != null) {
                        val parameterTypes = method.getParameterTypes()!!

                        // TODO in java 7 its not easy with reflection to get the parameter argument name...
                        var counter = 0
                        val parameters = parameterTypes.map<Class<out Any?>?, String>{ "arg${++counter}: ${simpleTypeName(it)}" }.makeString(", ")
                        val returnType = simpleTypeName(method.getReturnType())
                        println("    fun ${method.getName()}($parameters): $returnType = js.noImpl")
                    }
                }
            }
            println("}")
            println("")
        }
    }
}

fun write(file: File, block: PrintWriter.() -> Unit): Unit {
    println("Generating file: ${file.getCanonicalPath()}")
    val writer = PrintWriter(FileWriter(file))
    writer.use { writer.block() }
}