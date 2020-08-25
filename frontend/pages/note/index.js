import React, { useState, Component } from 'react'
import { Editor, EditorState, convertFromRaw, RichUtils, convertToRaw } from 'draft-js'
import Cookie from 'js-cookie'

export default class App extends Component {

    constructor(props) {
        super(props)
        const initJwt = Cookie.get('jwt')
        this.state = {
            editorState: EditorState.createEmpty(),
            jwt: initJwt ? initJwt : ''
        }
        this.focus = () => this.editor.focus()
        this.onChange = async (editorState) => {
            const oldContent = this.state.editorState.getCurrentContent()
            const newContent = editorState.getCurrentContent()
            
            //the await here is used to wait for the set state to finish
            //without await, the save is always one input behind
            await this.setState({ ...this.state, editorState: editorState })
            if(oldContent != newContent) {
                console.log('save')
                this.save()
            }
        }
        this.getFromApi = this.getFromApi.bind(this)
        this.getFromApi()
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
                title: 'foo\'s note', //todo titles
                body: convertToRaw(this.state.editorState.getCurrentContent())
            })
        }

        const response = await (await fetch('http://localhost:8080/api/v1/notes/save', requestOptions)).text()
    }

    getFromApi = async () => {
      if(this.state.jwt === '') return;
      const requestOptions = {
          method: 'GET',
          headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + this.state.jwt}
      }
      const response = await (await fetch('http://localhost:8080/api/v1/notes/note/foo%27s+note/raw', requestOptions)).text()
      if(response === '') {
          this.setState({ ...this.state, editorState: EditorState.createEmpty() })
      } else {
          console.log(parsedResponse)
          const parsedResponse = JSON.parse(response)
          const responseFromRaw = convertFromRaw(parsedResponse)
          const responseEditorState = EditorState.createWithContent(responseFromRaw)
          this.setState({ ...this.state, editorState: responseEditorState })
          console.log(this.state.editorState)
      }
      
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