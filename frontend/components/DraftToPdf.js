import { stateToHTML } from "draft-js-export-html"
import pdfMake from 'pdfmake/build/pdfmake'
import htmlToPdfmake from 'html-to-pdfmake'
import * as pdfFonts from 'pdfmake/build/vfs_fonts';

//declaring fonts
pdfMake.vfs = pdfFonts.pdfMake.vfs
pdfMake.fonts = {
    Roboto: {
        normal: 'https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.66/fonts/Roboto/Roboto-Regular.ttf'
    },
    Courier: {
        normal: 'http://fontsfree.net//wp-content/fonts/basic/fixed-width/FontsFree-Net-SLC_.ttf'
    }
}

export default function draftToPdf(contentState, styleMap, title) {

    //converting to html

    let exportHtmlStyles = {}

    //reformatting style map so that it's compatible with draft-js-export-html
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
        //used for block styles (text aligning)
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
    
    //converting html to pdfmake compatible input
    var pdfMakeInput = htmlToPdfmake(html)
    //putting it into a content object so that it's actually pdfmake compatible
    pdfMakeInput = {content: [pdfMakeInput]}
    
    //converting to Courier so that google docs can read it better (maybe add option to disable)
    //google docs has trouble reading ligatures in Courier, but a main goal of this is to be
    //compatible with docs
    pdfMakeInput.defaultStyle = {
        font: 'Courier'
    }

    //downloading
    pdfMake.createPdf(pdfMakeInput).download(title)
    
}
