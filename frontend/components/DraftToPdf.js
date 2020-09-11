import { stateToHTML } from "draft-js-export-html"
import jsPDF from "jspdf"

//todo word wrap not working
export default function draftToPdf(contentState, styleMap, title) {

    //converting to html

    let exportHtmlStyles = {}
    for(var child of Object.entries(styleMap)) {
        const attr = child[1]
        const key = Object.keys(attr)[0]
        const val = Object.values(attr)[0]
        const childName = child[0]
        exportHtmlStyles = {
            ...exportHtmlStyles, 
            [childName]: {
                style: {
                    [key]: val
                }
            }
        }
    }

    let exportHtmlOptions = {
        inlineStyles: {
            ...exportHtmlStyles
        }
    }
    //todo block styles
    let html = stateToHTML(contentState, exportHtmlOptions)
    html = `<div style="white-space: nowrap;">` + html + '</div>'
    console.log(html)

    //converting to pdf
    var pdf = new jsPDF('p', 'pt', 'a4')
    
    pdf.html(
        html,
        {
            callback: function (pdf) {
                pdf.save(title);
            },
            x: 30,
            y: 25
        }
    )

}