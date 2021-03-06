package pers.mrwangx.tools.musictool.cell;

import com.jfoenix.controls.JFXListCell;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import pers.mrwangx.tool.musictool.config.MusicAPIConfig;
import pers.mrwangx.tool.musictool.entity.Song;
import pers.mrwangx.tools.musictool.App;
import pers.mrwangx.tools.musictool.controller.MainController;
import pers.mrwangx.tools.musictool.controller.MyFavoriteController;
import pers.mrwangx.tools.musictool.service.Data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * \* Author: MrWangx
 * \* Date: 2019/6/12
 * \* Time: 20:05
 * \* Description:
 **/
public class SongsListViewCell extends JFXListCell<Song> {

    private Data<Song> data;

    public SongsListViewCell(Data<Song> data) {
        this.data = data;
    }

    @Override
    protected void updateItem(Song item, boolean empty) {
        if (item != null) {
            Parent p = null;
            try {
                p = FXMLLoader.load(this.getClass().getResource("/fxml/songsitem.fxml"));
                this.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                        data.setCrtindex(getIndex());
                        MainController.mainController.playMusic(item);
                        MainController.mainController.setData(data);
                    }
                });
                Label songname = (Label) p.lookup("#songname");
                Label songinfo = (Label) p.lookup("#songinfo");
//                Label status = (Label) p.lookup("#status");
                ImageView logo = (ImageView) p.lookup("#logo");
                switch (item.getMusicType()) {
                    case MusicAPIConfig.MUSIC_TYPE_TECENT:
                        logo.setImage(App.qqmusic_logo);
                        break;
                    case MusicAPIConfig.MUSIC_TYPE_NETEASE:
                        logo.setImage(App.netease_logo);
                        break;
                    case MusicAPIConfig.MUSIC_TYPE_KUGOU:
                        logo.setImage(App.kugou_logo);
                        break;
                    case MusicAPIConfig.MUSIC_TYPE_KUWO:
                        logo.setImage(App.kuwo_logo);
                        break;
                    case MusicAPIConfig.MUSIC_TYPE_BAIDU:
                        logo.setImage(App.baidu_logo);
                        break;
                }
                ImageView download = (ImageView) p.lookup("#download");
                ImageView like = (ImageView) p.lookup("#like");
                songname.setText(item.getName());
                songinfo.setText(item.getSinger() + "-" + item.getAlbumname() + "\t" + item.getFormatTime());
                download.setOnMouseClicked(event -> {
                    Task<String> downloadTask = new Task<String>() {
                        @Override
                        protected String call() throws Exception {
                            updateMessage("建立连接...");
                            try {
                                URL url = new URL(item.REAL_SONG_PLAY_URL());
                                try (InputStream inputStream = url.openStream();
                                     FileOutputStream fout = new FileOutputStream(new File(MainController.mainController.getSavePath(), item.getName() + " - " + item.getSinger()) + ".mp3");
                                ) {
                                    updateMessage("建立连接成功...");
                                    updateMessage("下载中...");
                                    int length = 0;
                                    byte[] data = new byte[1024];
                                    while ((length = inputStream.read(data, 0, data.length)) != -1) {
                                        fout.write(data, 0, length);
                                        fout.flush();
                                    }
                                    updateMessage("已下载");
                                } catch (Exception e) {
                                    updateMessage("下载失败...");
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    };
                    new Thread(downloadTask).start();
                });
                like.setOnMouseClicked(event -> {
                    MyFavoriteController.myFavoriteController.addToMyFavorite(item);
                });
                setGraphic(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}
