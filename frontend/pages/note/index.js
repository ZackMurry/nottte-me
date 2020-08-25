import React, { useState, Component } from 'react'
import { Editor, EditorState, convertFromRaw, RichUtils, convertToRaw } from 'draft-js'
import Cookie from 'js-cookie'

const initialData = {
    blocks: [
      {
        key: '16d0k',
        text: 'You can edit this text.',
        type: 'unstyled',
        depth: 0,
        inlineStyleRanges: [{ offset: 0, length: 23, style: 'BOLD' }],
        entityRanges: [],
        data: {},
      },
      {
        key: '98peq',
        text: '',
        type: 'unstyled',
        depth: 0,
        inlineStyleRanges: [],
        entityRanges: [],
        data: {},
      },
      {
        key: 'ecmnc',
        text:
          'Luke Skywalker has vanished. In his absence, the sinister FIRST ORDER has risen from the ashes of the Empire and will not rest until Skywalker, the last Jedi, has been destroyed.',
        type: 'unstyled',
        depth: 0,
        inlineStyleRanges: [
          { offset: 0, length: 14, style: 'BOLD' },
          { offset: 133, length: 9, style: 'BOLD' },
        ],
        entityRanges: [],
        data: {},
      },
      {
        key: 'fe2gn',
        text: '',
        type: 'unstyled',
        depth: 0,
        inlineStyleRanges: [],
        entityRanges: [],
        data: {},
      },
      {
        key: '4481k',
        text:
          'With the support of the REPUBLIC, General Leia Organa leads a brave RESISTANCE. She is desperate to find her brother Luke and gain his help in restoring peace and justice to the galaxy.',
        type: 'unstyled',
        depth: 0,
        inlineStyleRanges: [
          { offset: 34, length: 19, style: 'BOLD' },
          { offset: 117, length: 4, style: 'BOLD' },
          { offset: 68, length: 10, style: 'ANYCUSTOMSTYLE' },
        ],
        entityRanges: [],
        data: {},
      },
    ],
    entityMap: {},
  }

export default class App extends Component {

    constructor(props) {
        super(props)
        const initJwt = Cookie.get('jwt')
        this.state = {
            editorState: EditorState.createWithContent(convertFromRaw(initialData)),
            jwt: initJwt ? initJwt : ''
        }
        this.focus = () => this.editor.focus()
        this.onChange = (editorState) => {
            const oldContent = this.state.editorState.getCurrentContent()
            const newContent = editorState.getCurrentContent()
            this.setState({ editorState: editorState })
            if(oldContent != newContent) {
                console.log('save')
                this.save()
            }
        }
    }

    handleKeyCommand = (command) => {
        const { editorState } = this.state
        const newState = RichUtils.handleKeyCommand(editorState, command)
        if (newState) {
          this.onChange(newState)
          return true
        }
        return false
    }

    //todo call this less
    save = async () => {
        const requestOptions = {
            method: 'PATCH',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + this.state.jwt},
            body: JSON.stringify({
                title: 'title', //todo titles
                body: convertToRaw(this.state.editorState.getCurrentContent())
            })
        }

        const response = await (await fetch('http://localhost:8080/api/v1/notes/save', requestOptions)).text()
        console.log(response)
    }

    render() {
        const { editorState } = this.state
        return (
            <Editor
                editorState={editorState}
                handleKeyCommand={this.handleKeyCommand}
                onChange={this.onChange}
            />
        )
        
    }


}