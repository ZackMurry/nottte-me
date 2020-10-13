import { CircularProgress, Typography } from '@material-ui/core'
import DoneIcon from '@material-ui/icons/Done'
import CloseIcon from '@material-ui/icons/Close'

export default function SaveStatus({ status = 's' }) {
    return (
        <div
            style={{
                position: 'fixed',
                bottom: '1vw',
                left: '1vw',
                width: '4vw',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
            }}
        >
            {
                status === 's' && (
                    <>
                        <DoneIcon color='primary' />
                        <Typography color='primary' style={{ fontWeight: 300 }}>
                            Saved
                        </Typography>
                    </>
                )
            }
            {
                status === 'u' && (
                    <>
                        <CloseIcon color='primary' />
                        <Typography color='primary' style={{ fontWeight: 300 }}>
                            Unsaved
                        </Typography>
                    </>
                )
            }
            {
                status === 'g' && (
                    <>
                        <div style={{ paddingRight: 10 }}>
                            <CircularProgress size='1rem' />
                        </div>
                        <Typography color='primary' style={{ fontWeight: 300 }}>
                            Saving...
                        </Typography>
                    </>
                )
            }
        </div>
    )
}
