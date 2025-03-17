class StreamingService : Service() {
    private val notificationId = 101
    private var ffmpegProcess: Process? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.extras?.let {
            val fileUri = Uri.parse(it.getString("FILE_URI"))
            val streamKey = it.getString("STREAM_KEY") ?: return START_NOT_STICKY
            
            startForeground(notificationId, createNotification())
            startStreaming(fileUri, streamKey)
        }
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val channelId = createNotificationChannel()
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Streaming Active")
            .setContentText("Streaming to YouTube")
            .setSmallIcon(R.drawable.ic_notification)
            .build()
    }

    private fun createNotificationChannel(): String {
        val channelId = "stream_channel"
        val channelName = "Stream Service"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
        return channelId
    }

    private fun startStreaming(fileUri: Uri, streamKey: String) {
        val rtmpUrl = "rtmp://a.rtmp.youtube.com/live2/$streamKey"
        
        try {
            val cmd = arrayOf(
                "ffmpeg",
                "-re",
                "-i", fileUri.path,
                "-c:v", "libx264",
                "-preset", "ultrafast",
                "-b:v", "2000k",
                "-c:a", "aac",
                "-f", "flv",
                rtmpUrl
            )

            ffmpegProcess = Runtime.getRuntime().exec(cmd)
        } catch (e: IOException) {
            showErrorNotification("Stream failed to start")
        }
    }

    private fun showErrorNotification(message: String) {
        val notification = NotificationCompat.Builder(this, "errors")
            .setContentTitle("Stream Error")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_error)
            .build()
        
        NotificationManagerCompat.from(this)
            .notify(notificationId + 1, notification)
    }

    override fun onDestroy() {
        ffmpegProcess?.destroy()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
