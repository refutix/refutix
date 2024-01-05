/* Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License. */

import styles from './index.module.scss'
import MenuTree from './components/menu-tree';
import EditorTabs from './components/tabs';
import EditorDebugger from './components/debugger';
import * as monaco from 'monaco-editor'
import MonacoEditor from '@/components/monaco-editor';
import EditorConsole from './components/console';
import { format } from 'sql-formatter';
import { useMessage } from 'naive-ui'

export default defineComponent({
  name: 'QueryPage',
  setup() {
    const message = useMessage()

    const editorVariables = reactive({
      editor: {} as any,
      language: 'sql'
    })

    const editorMounted = (editor: monaco.editor.IStandaloneCodeEditor) => {
      editorVariables.editor = editor
    }

    const handleFormat = () => {
      toRaw(editorVariables.editor).setValue(format(toRaw(editorVariables.editor).getValue()))
    }

    const editorSave = () => {
      message.success('Save success')
      tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab).content = toRaw(editorVariables.editor).getValue()
      handleFormat()
      tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab).isSaved = true
    }

    const handleContentChange = (value: string) => {
      tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab).content = value
      tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab).isSaved = false
    }

    const consoleHeightType = ref('down')

    const handleConsoleUp = (type: string) => {
      consoleHeightType.value = type
      editorHeight.value = '100%'
      consoleHeight.value = '0%'
    }

    const handleConsoleDown = (type: string) => {
      consoleHeightType.value = type
      editorHeight.value = '60%'
      consoleHeight.value = '40%'
    }

    watch(
      () => consoleHeightType.value,
      () => {
        if (tabData.value.panelsList?.length > 0) {
          editorVariables.editor?.layout()
        }
      }
    )

    // mitt - handle tab choose
    const tabData = ref({}) as any
    const { mittBus }  = getCurrentInstance()!.appContext.config.globalProperties
    mittBus.on('initTabData', (data: any) => {
      tabData.value = data
    })

    const menuTreeWidth = ref('20%');
    const isResizing = ref(false);

    const startMenuTreeResize = (event: MouseEvent) => {
      isResizing.value = true;
      event.preventDefault();
    };

    const doMenuTreeResize = (event: MouseEvent) => {
      if (isResizing.value) {
        const parentWidth = document.documentElement.clientWidth;
        let newWidth = event.clientX;

        let widthInPercent = (newWidth / parentWidth) * 100;

        widthInPercent = Math.max(18, Math.min(widthInPercent, 30));

        menuTreeWidth.value = `${widthInPercent}%`;
      }
    };

    const stopMenuTreeResize = () => {
      isResizing.value = false;
    };

    const editorAreaStyle = computed(() => {
      const menuWidthPercent = parseFloat(menuTreeWidth.value);
      return {
        width: `calc(100% - ${menuWidthPercent}%)`
      };
    });

    const editorHeight = ref('60%');
    const consoleHeight = ref('40%');
    const isConsoleResizing = ref(false);

    const startConsoleResize = (event: MouseEvent) => {
      isConsoleResizing.value = true;
      event.preventDefault();
    };

    const doConsoleResize = (event: MouseEvent) => {
      if (isConsoleResizing.value) {
        const parentHeight = document.documentElement.clientHeight;
        let newConsoleHeight = parentHeight - event.clientY;

        let consoleHeightPercent = (newConsoleHeight / parentHeight) * 100;
        consoleHeightPercent = Math.max(20, Math.min(consoleHeightPercent, 100));

        editorHeight.value = `${100 - consoleHeightPercent}%`;
        consoleHeight.value = `${consoleHeightPercent}%`;
      }
    };

    const stopConsoleResize = () => {
      isConsoleResizing.value = false;
    };

    onMounted(() => {
      document.addEventListener('mousemove', doMenuTreeResize);
      document.addEventListener('mouseup', stopMenuTreeResize);
      document.addEventListener('mousemove', doConsoleResize);
      document.addEventListener('mouseup', stopConsoleResize);
    });

    onBeforeUnmount(() => {
      document.removeEventListener('mousemove', doMenuTreeResize);
      document.removeEventListener('mouseup', stopMenuTreeResize);
      document.removeEventListener('mousemove', doConsoleResize);
      document.removeEventListener('mouseup', stopConsoleResize);
    });

    const showConsole = ref(false);
    const handleConsoleClose = () => {
      consoleHeight.value = '0%';
      editorHeight.value = '100%';
    };

    return {
      ...toRefs(editorVariables),
      editorMounted,
      editorSave,
      handleContentChange,
      handleFormat,
      tabData,
      handleConsoleUp,
      handleConsoleDown,
      consoleHeightType,
      menuTreeWidth,
      startMenuTreeResize,
      editorAreaStyle,
      startConsoleResize,
      consoleHeight,
      editorHeight,
      handleConsoleClose,
      showConsole
    }
  },
  render() {
    return (
      <div class={styles.query}>
        <div style={{ width: this.menuTreeWidth }}>
          <MenuTree />
        </div>
        <div class={styles.splitter} onMousedown={this.startMenuTreeResize}></div>
        <div class={styles['editor-area']} style={this.editorAreaStyle}>
          <n-card class={styles.card} content-style={'padding: 5px 18px;display: flex;flex-direction: column;'}>
            <div class={styles.tabs}>
              <EditorTabs />
            </div>
            <div class={styles.debugger}>
              <EditorDebugger onHandleFormat={this.handleFormat} onHandleSave={this.editorSave} />
            </div>
            <div class={styles.editor} style={{ height: this.editorHeight }}>
              {
                this.tabData.panelsList?.length > 0 &&
                <n-card content-style={'padding: 0;'}>
                  <MonacoEditor
                    v-model={this.tabData.panelsList.find((item: any) => item.key === this.tabData.chooseTab).content}
                    language={this.language}
                    onEditorMounted={this.editorMounted}
                    onEditorSave={this.editorSave}
                    onChange={this.handleContentChange}
                    style={'height: 100%'}
                  />
                </n-card>
              }
            </div>
            <div class={styles['console-splitter']} onMousedown={this.startConsoleResize}></div>
            <div class={styles.console} style={{ height: this.consoleHeight }}>
              {
                this.tabData.panelsList?.length > 0 &&
                <n-card content-style={'padding: 0;'}>
                  <EditorConsole onConsoleDown={this.handleConsoleDown} onConsoleUp={this.handleConsoleUp} onConsoleClose={this.handleConsoleClose}/>
                </n-card>
              }
            </div>
          </n-card>
        </div>
      </div>
    );
  }
});
