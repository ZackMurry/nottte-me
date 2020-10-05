import {
    Paper, Typography, Card, CardContent, CardActions, IconButton
} from '@material-ui/core'
import GetAppIcon from '@material-ui/icons/GetApp'
import { Editor } from 'draft-js'

export default function DownloadWithPreview({
    name, editorState, styleMap, blockStyleFn, onClick
}) {
    return (
        <div style={{ cursor: 'pointer' }} onClick={onClick}>
            <Card elevation={3}>
                <CardContent>
                    <div
                        style={{
                            backgroundColor: '#2d323e',
                            width: '100%',
                            height: '35vh',
                            borderRadius: 10,
                            margin: 0,
                            padding: 0,
                            display: 'flex'
                        }}
                    >
                        <Paper
                            elevation={0}
                            style={{
                                width: '60%',
                                marginLeft: 'auto',
                                marginRight: 'auto',
                                marginBottom: 0,
                                height: '60%',
                                verticalAlign: 'bottom',
                                alignSelf: 'flex-end',
                                borderRadius: '10px 10px 0 0',
                                overflow: 'hidden'
                            }}
                        >
                            <Editor
                                editorState={editorState}
                                onChange={() => onClick()}
                                customStyleMap={styleMap}
                                editorKey='preview'
                                blockStyleFn={blockStyleFn}
                            />
                        </Paper>
                    </div>
                </CardContent>
                <CardActions style={{
                    display: 'flex', justifyContent: 'space-between', width: '95%', margin: '0 auto'
                }}
                >
                    <Typography variant='h4' style={{ marginLeft: 10 }}>{ name }</Typography>
                    <IconButton>
                        <GetAppIcon fontSize='large' />
                    </IconButton>
                </CardActions>

            </Card>

        </div>

    )
}
