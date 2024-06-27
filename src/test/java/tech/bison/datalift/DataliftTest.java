package tech.bison.datalift;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DataliftTest {

  @Mock
  private Versioner versioner;
  @Mock
  private ScriptLoader scriptLoader;
  @Mock
  private Runner runner;

  @Test
  void bla() {
    Context context = null;
    DataLift dataLift = new DataLift(versioner, scriptLoader, runner);
    dataLift.execute(context);
  }
}
