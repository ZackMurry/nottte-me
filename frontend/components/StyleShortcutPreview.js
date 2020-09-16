import React, { useEffect, useState } from 'react'
import { Grid, Typography, IconButton, TextField, Grow } from '@material-ui/core'
import EditIcon from '@material-ui/icons/Edit';
import DoneIcon from '@material-ui/icons/Done';
import PlainTooltip from './PlainTooltip';
import DeleteIcon from '@material-ui/icons/Delete';
import AddIcon from '@material-ui/icons/Add';
import RemoveIcon from '@material-ui/icons/Remove';

export default function StyleShortcutPreview({ name, button, attributes, update, jwt, onError, showError, deleteSelf }) {

    console.log('key: ' + button)

    const [ editMode, setEditMode ] = useState(false)
    const [ editedKey, setEditedKey ] = useState(button + '')
    const [ editedAttributes, setEditedAttributes ] = useState(attributes)
    const [ addingAttribute, setAddingAttribute ] = useState(false)
    const [ currentAddingAttribute, setCurrentAddingAttribute ] = useState({attribute: '', value: ''}) //attribute that user is currently adding

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

    const handleRemoveAttribute = async (index) => {
        if(editedAttributes.length == 1) {
            handleDelete()
            return
        }

        const attribute = editedAttributes[index]
        const attrAttr = attribute.attribute //this is a really bad name

        //sending to server
        const requestOptions = {
            method: 'DELETE',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
        }

        const response = await fetch(`http://localhost:8080/api/v1/users/principal/preferences/shortcuts/style/${name}/attribute/${attrAttr}`, requestOptions)
        console.log(response.status)
        if(response.status == 200) {
            editedAttributes.splice(index, 1)
            update(name, button, editedAttributes)
        }
        //todo show user if fails
    }

    //deletes shortcut
    const handleDelete = async () => {
        //sending to server
        const requestOptions = {
            method: 'DELETE', 
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
        }
        const response = await fetch(`http://localhost:8080/api/v1/users/principal/preferences/shortcuts/style/${name}`, requestOptions)
        console.log(response.status)

        if(response.status == 200) {
            deleteSelf()
        }
    }

    const handleFinishAttribute = async () => {
        //sending to server
        const requestOptions = {
            method: 'POST',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt},
            body: JSON.stringify(currentAddingAttribute)
        }

        const response = await fetch(`http://localhost:8080/api/v1/users/principal/preferences/shortcuts/style/${name}/attributes`, requestOptions)
        console.log(response.status)

        if(response.status == 200) {
            await setEditedAttributes([...editedAttributes, currentAddingAttribute])
            update(name, button, editedAttributes)
            setCurrentAddingAttribute({attribute: '', value: ''})
            setAddingAttribute(false)
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
                        <Grid item xs={12} lg={2}>
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
                                        i > 1 && <Grid item xs={12} lg={5}></Grid>
                                    }
                                    {
                                        i == 1 && (
                                            <>
                                                <Grid item xs={3} lg={1}>
                                                    <PlainTooltip title='Done editing' >
                                                        <IconButton onClick={handleDone} style={{padding: 0}}>
                                                            <DoneIcon color='secondary' />
                                                        </IconButton>
                                                    </PlainTooltip>
                                                </Grid>
                                                <Grid item xs={3} lg={1}>
                                                    <PlainTooltip title='Delete shortcut'>
                                                        <IconButton onClick={handleDelete} style={{padding: 0}}>
                                                            <DeleteIcon color='secondary' />
                                                        </IconButton>
                                                    </PlainTooltip>
                                                </Grid>
                                                <Grid item xs={3} lg={1}>
                                                    <PlainTooltip title='Add attribute'>
                                                        <IconButton onClick={() => setAddingAttribute(!addingAttribute)} style={{padding: 0}}>
                                                            <AddIcon color='secondary' />
                                                        </IconButton>
                                                    </PlainTooltip>
                                                </Grid>
                                                
                                                <Grid item xs={3} lg={2}></Grid>
                                            </>
                                        )
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
                                    <Grid item xs={2} lg={1}>
                                        <PlainTooltip title={'Remove attribute' + (editedAttributes.length == 1 ? '. This will remove this shortcut, too.': '')}>
                                            <IconButton onClick={() => handleRemoveAttribute(i)} style={{padding: 0}}>
                                                <RemoveIcon color='secondary' />
                                            </IconButton>
                                        </PlainTooltip>                
                                    </Grid>
                                    
                                </React.Fragment>
                            ))
                        }
                        {
                            editedAttributes.length === 1 && (
                                <>
                                    <Grid item xs={3} lg={1}>
                                        <PlainTooltip title='Done editing' >
                                            <IconButton onClick={handleDone} style={{padding: 0}}>
                                                <DoneIcon color='secondary' />
                                            </IconButton>
                                        </PlainTooltip>
                                    </Grid>
                                    <Grid item xs={3} lg={1}>
                                        <PlainTooltip title='Delete shortcut'>
                                            <IconButton onClick={handleDelete} style={{padding: 0}}>
                                                <DeleteIcon color='secondary' />
                                            </IconButton>
                                        </PlainTooltip>
                                    </Grid>
                                    <Grid item xs={3} lg={1}>
                                        <PlainTooltip title='Add attribute'>
                                            <IconButton onClick={() => setAddingAttribute(!addingAttribute)} style={{padding: 0}}>
                                                <AddIcon color='secondary' />
                                            </IconButton>
                                        </PlainTooltip>
                                    </Grid>     
                                    <Grid item xs={2}></Grid>                       
                                </>
                            )
                        }
                        {
                            addingAttribute && (
                                <>
                                    {
                                        editedAttributes.length > 1 && <Grid xs={3} lg={5}></Grid>
                                    }
                                    <Grid item xs={6} lg={3}>
                                        <TextField 
                                            value={currentAddingAttribute.attribute}
                                            //onKeyDown={e => handleEnterDetection(e)} change this to add the attribute
                                            onChange={e => setCurrentAddingAttribute({...currentAddingAttribute, attribute: e.target.value})}
                                            style={{padding: 0, width: '100%'}}
                                            error={currentAddingAttribute.attribute.length <= 0}
                                            helperText={currentAddingAttribute.attribute.length <= 0 ? 'attribute should not be empty' : ''}
                                            rowsMax={3}
                                            multiline
                                        />
                                    </Grid>
                                    <Grid item xs={6} lg={3}>
                                        <TextField
                                            value={currentAddingAttribute.value}
                                            //onKeyDown={e => handleEnterDetection(e)}
                                            onChange={e => setCurrentAddingAttribute({...currentAddingAttribute, value: e.target.value})}
                                            style={{padding: 0, width: '100%'}}
                                            error={currentAddingAttribute.value.length <= 0}
                                            helperText={currentAddingAttribute.value.length <= 0 ? 'value should not be empty' : ''}
                                        />
                                    </Grid>
                                    <Grid item xs={1}>
                                        <PlainTooltip title='Finish adding attribute'>
                                            <IconButton onClick={handleFinishAttribute}>
                                                <DoneIcon color='secondary' />
                                            </IconButton>
                                        </PlainTooltip>
                                    </Grid>
                                </>
                            )
                        }
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
                                    {
                                        i === 0 && (
                                            <Grid item xs={12} lg={1}>
                                                <IconButton onClick={() => {
                                                        setEditMode(true)
                                                        //resetting values to actual ones, helps with delay in fetching
                                                        setEditedKey(button)
                                                        setEditedAttributes(attributes)
                                                    }} 
                                                    style={{padding: 0}}
                                                >
                                                    <EditIcon color='secondary' />
                                                </IconButton>
                                            </Grid>
                                        )
                                    }
                                </React.Fragment>
                            ))
                            
                        }
                        
                    </Grid>
                )
            }
        </div>
        
    )

}
