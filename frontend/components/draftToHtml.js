import { stateToHTML } from "draft-js-export-html"


export default function draftToHtml(contentState, styleMap) {
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

    return stateToHTML(contentState, exportHtmlOptions)
}
