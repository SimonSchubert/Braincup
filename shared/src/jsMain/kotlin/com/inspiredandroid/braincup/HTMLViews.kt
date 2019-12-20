package com.inspiredandroid.braincup

import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onInputFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import kotlin.browser.document
import kotlin.browser.window

internal fun FlowContent.textButton(
    text: String, width: Int = -1,
    imagePath: String? = null, color: String? = null, onClick: () -> Unit
) {
    var extraStyle = if (width == -1) {
        ""
    } else {
        "width: ${width}px;"
    }
    extraStyle += if (color == null) {
        ""
    } else {
        "--mdc-theme-primary: $color;"
    }
    button {
        style =
            "$extraStyle max-width: 70%; height: 50px; font-size: 16px; margin-top: 16px; margin-right: 6px"
        classes += "mdc-button mdc-button--raised"
        if (imagePath != null) {
            img {
                classes += "material-icons mdc-button__icon"
                src = imagePath
                style = "height: 20px; width: 20px;"
            }
        }
        span {
            classes += "mdc-button__label"
            text(text)
        }
        onClickFunction = {
            onClick()
        }
    }
}

internal fun FlowContent.giveUpButton(
    answer: (String) -> Unit,
    next: () -> Unit
) {
    button {
        style = "width: 150px"
        classes += "mdc-button mdc-button--raised"
        text("Give up")
        onClickFunction = {
            answer("")
            window.setTimeout({
                next()
            }, 1000)
        }
    }
}

internal fun FlowContent.helperText(text: String) {
    div {
        classes += "mdc-text-field-helper-line"
        style = "display: block;"
        div {
            classes += "mdc-text-field-helper-text"
            style = "opacity: 1;color: white;"
            text(text)
        }
    }
}

internal fun FlowContent.textInput(width: Int = 150, action: (String) -> Unit) {
    val randomId = randomString()
    div {
        classes += "mdc-text-field mdc-text-field--outlined"
        style = "max-width: 100%;"
        input {
            style = "text-align: center;font-size: 30px;width: ${width}px;max-width: 100%;"
            classes += "mdc-text-field__input"
            id = randomId
            autoComplete = false
            onInputFunction = {
                val input = document.getElementById(randomId) as HTMLInputElement
                input.focus()
                action(input.value)
            }
        }
        div {
            classes += "mdc-notched-outline mdc-notched-outline--no-label"
            div {
                classes += "mdc-notched-outline__leading"
            }
            div {
                classes += "mdc-notched-outline__trailing"
            }
        }
    }
}

internal fun FlowContent.multilineTextInput(width: Int = 350, action: (String) -> Unit) {
    val randomId = randomString()
    div {
        classes += "mdc-text-field mdc-text-field--textarea"
        style = "max-width: 100%;"
        textArea {
            style = "width: ${width}px; height: 250px; max-width: 100%;"
            classes += "mdc-text-field__input"
            id = randomId
            onInputFunction = {
                val input = document.getElementById(randomId) as HTMLTextAreaElement
                input.focus()
                action(input.value)
                console.log("input: ${input.value}")
            }
        }
        div {
            classes += "mdc-notched-outline mdc-notched-outline--no-label"
            div {
                classes += "mdc-notched-outline__leading"
            }
            div {
                classes += "mdc-notched-outline__trailing"
            }
        }
    }
}

internal fun FlowContent.title(title: String) {
    headline2(title)
}

internal fun FlowContent.headline6(text: String) {
    div {
        classes += "mdc-typography--headline6"
        text(text)
    }
}

internal fun FlowContent.headline5(text: String) {
    div {
        classes += "mdc-typography--headline5"
        text(text)
    }
}

internal fun FlowContent.headline4(text: String) {
    div {
        classes += "mdc-typography--headline4"
        text(text)
    }
}

internal fun FlowContent.headline3(text: String) {
    div {
        classes += "mdc-typography--headline3"
        text(text)
    }
}

internal fun FlowContent.headline2(text: String) {
    div {
        classes += "mdc-typography--headline2"
        text(text)
    }
}

internal fun FlowContent.illustration(id: String) {
    img {
        classes += "illustration"
        src = "images/$id"
    }
}

internal fun FlowContent.margin(margin: Int) {
    div {
        style = "margin-top: ${margin}px"
    }
}

internal fun FlowContent.pentagonText(title: String, value: String) {
    div {
        classes += "pentagonWrapper"
        div {
            classes += "pentagon"
            headline6(title)
            headline4(value)
        }
    }
}