import React, { useEffect, useState } from 'react'
import { Grid, Typography, IconButton, TextField, Grow } from '@material-ui/core'
import EditIcon from '@material-ui/icons/Edit';
import DoneIcon from '@material-ui/icons/Done';

//todo deleting
export default function StyleShortcutPreview({ name, button, attributes, update, jwt, onError, showError }) {

    const [ editMode, setEditMode ] = useState(false)
    const [ editedKey, setEditedKey ] = useState(button)
    const [ editedAttributes, setEditedAttributes ] = useState(attributes)

    const handleKeyDown = (e) => {
        setEditedKey(e.key)
        e.preventDefault()
    }

    useEffect(() => {
        setEditedAttributes(attributes)
    }, [ attributes ])

    const handleDone = async () => {
        setEditMode(false)
        
        //checking if anything has been changed
        if(editedKey == button && editedAttributes == attributes) return
        
        
        
        //sending to server
        const requestOptions = {
            method: 'PATCH',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt},
            body: JSON.stringify({
                name: name,
                key: editedKey,
                attributes: editedAttributes
            })
        }

        const response = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/style/' + encodeURI(name), requestOptions)

        if(response.status == 400) {
            onError('Unable to update style shortcut: error code 400')
            showError(true)
            return
        }

        //sends to parent
        update(name, editedKey, editedAttributes)
    }

    //automatically finishes editing when enter is pressed on text field
    const handleEnterDetection = (e) => {
        if(e.key == 'Enter') {
            handleDone()
        }
    }

    return (
        <div>
            {
                editMode
                ?
                (
                    <Grid container spacing={3}>
                        <Grid item xs={12} lg={3}>
                            <Typography>
                                {name}
                            </Typography>
                        </Grid>
                        <Grid item xs={12} lg={2} style={{paddingTop: 0, paddingBottom: 0}}>
                        <TextField 
                            value={editedKey} 
                            style={{width: '100%', caretColor: 'transparent'}} 
                            spellCheck='false'
                            onKeyDown={handleKeyDown}
                        />
                        </Grid>
                        {
                            editedAttributes && editedAttributes.map((editedAttribute, i) => (
                                <React.Fragment key={i}>
                                    {
                                        i >= 1 && <Grid item xs={12} lg={5}></Grid>
                                    }
                                    <Grid item xs={12} lg={3}>
                                        <TextField 
                                            value={editedAttribute.attribute}
                                            onKeyDown={e => handleEnterDetection(e)}
                                            onChange={e => {
                                                let tempAttrs = editedAttributes.slice()
                                                tempAttrs[i] = {...tempAttrs[i], attribute: e.target.value}
                                                setEditedAttributes(tempAttrs)
                                            }}
                                            style={{padding: 0, width: '100%'}}
                                            error={editedAttribute.attribute.length <= 0}
                                            helperText={editedAttribute.attribute.length <= 0 ? 'attribute should not be empty' : ''}
                                            rowsMax={3}
                                            multiline
                                        />
                                    </Grid>
                                    <Grid item xs={12} lg={3}>
                                        <TextField
                                            value={editedAttribute.value}
                                            onKeyDown={e => handleEnterDetection(e)}
                                            onChange={e => {
                                                let tempAttrs = editedAttributes.slice()
                                                tempAttrs[i] = {...tempAttrs[i], value: e.target.value}
                                                setEditedAttributes(tempAttrs)
                                            }}
                                            style={{padding: 0, width: '100%'}}
                                            error={editedAttribute.value.length <= 0}
                                            helperText={editedAttribute.value.length <= 0 ? 'value should not be empty' : ''}
                                        />
                                    </Grid>
                                    <Grid item xs={12} lg={1}>
                                        <IconButton onClick={handleDone} style={{padding: 0}}>
                                            <DoneIcon color='secondary' />
                                        </IconButton>
                                    </Grid>
                                    
                                </React.Fragment>
                            ))
                        }
                        {/* <Grid item xs={12} lg={3} style={{paddingTop: 0, paddingBottom: 0}}>
                            <TextField 
                                value={editedAttribute}
                                onKeyDown={e => handleEnterDetection(e)}
                                onChange={e => setEditedAttribute(e.target.value)}
                                style={{padding: 0, width: '100%'}}
                                error={editedAttribute.length <= 0}
                                helperText={editedAttribute.length <= 0 ? 'attribute should not be empty' : ''}
                                rowsMax={3}
                                multiline
                            />
                        </Grid>
                        <Grid item xs={12} lg={3} style={{paddingTop: 0, paddingBottom: 0}}>
                            <TextField
                                value={editedValue}
                                onKeyDown={e => handleEnterDetection(e)}
                                onChange={e => setEditedValue(e.target.value)}
                                style={{padding: 0, width: '100%'}}
                                error={editedValue.length <= 0}
                                helperText={editedValue.length <= 0 ? 'value should not be empty' : ''}
                            />
                        </Grid> */}
                        

                        {/* preview */}
                        {/* <Grid item xs={12} style={{padding: 0}}>
                            <Grow in timeout={1000}>
                                <div style={{textAlign: 'center'}}>
                                    <Typography variant='h4' style={{
                                        [editedAttribute]: editedValue
                                    }}>
                                        Preview
                                    </Typography>
                                </div>
                            </Grow>
                        </Grid> */}
                    </Grid>
                )
                :
                (
                    <Grid container spacing={3}>
                        <Grid item xs={12} lg={3}>
                            <Typography>{ name }</Typography>
                        </Grid>
                        <Grid item xs={12} lg={2}>
                            <Typography style={{fontWeight: 500}}>CTRL + { button }</Typography>
                        </Grid>
                        {
                            attributes && attributes.map(({attribute, value}, i) => (
                                <React.Fragment key={i}>
                                    {
                                        i !== 0 && (
                                            <Grid item xs={1} lg={5}></Grid>
                                        )
                                    }
                                    <Grid item xs={12} lg={3}>
                                        <Typography>{ attribute }</Typography>
                                    </Grid>
                                    <Grid item xs={12} lg={3}>
                                        <Typography>{ value }</Typography>
                                    </Grid>
                                </React.Fragment>
                            ))
                            
                        }
                        <Grid item xs={12} lg={1}>
                            <IconButton onClick={() => setEditMode(true)} style={{padding: 0}}>
                                <EditIcon color='secondary' />
                            </IconButton>
                        </Grid>
                    </Grid>
                )
            }
        </div>
        
    )

}
