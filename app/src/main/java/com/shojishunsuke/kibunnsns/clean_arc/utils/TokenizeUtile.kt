package com.shojishunsuke.kibunnsns.clean_arc.utils

import org.atilika.kuromoji.Token
import org.atilika.kuromoji.Tokenizer

fun tokenize(text:String):List<Token>{
    val tokenizer = Tokenizer.builder().build()
    return tokenizer.tokenize(text)
}
