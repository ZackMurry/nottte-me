import { stateToHTML } from "draft-js-export-html"
import pdfMake from 'pdfmake/build/pdfmake'
import htmlToPdfmake from 'html-to-pdfmake'
import * as pdfFonts from 'pdfmake/build/vfs_fonts';

pdfMake.vfs = pdfFonts.pdfMake.vfs

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
        },
        blockStyleFn: (block) => {
            const type = block.getType()
            if(type === 'right') {
                return {
                    style: {
                        textAlign: 'right'
                    }
                }
            } else if(type === 'center') {
                return {
                    style: {
                        textAlign: 'center'
                    }
                }
            }
        }
    }
    let html = stateToHTML(contentState, exportHtmlOptions)

    //converting to pdf
    var pdfMakeInput = htmlToPdfmake(html)
    pdfMakeInput = {content: [pdfMakeInput]}
    pdfMake.createPdf(pdfMakeInput).download(title)
    
}
